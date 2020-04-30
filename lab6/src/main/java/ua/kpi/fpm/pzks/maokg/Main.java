package ua.kpi.fpm.pzks.maokg;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import org.javatuples.Pair;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.function.Consumer;

public class Main extends JFrame implements ActionListener, KeyListener {
    private final static String camouflageTextureLocation = "textures/camouflage.jpg";
    private final static String helicopterModelLocation = "models/helicopter.obj";
    private final static String backgroundLocation = "backgrounds/in_the_sky.jpg";
    private final static float y_axis_rotate_initial = 0.627f;
    private final static float x_axis_rotate_initial = -0.084f;
    private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private final HashMap<String, Pair<Float, Boolean>> helicopter_pos = new HashMap<>();
    private final Timer timer = new Timer(20, this);
    private final BranchGroup root = new BranchGroup();
    private TransformGroup initTransformGroup;
    private boolean isFlyAway = false;
    private SimpleUniverse universe;
    private Canvas3D myCanvas3D;
    private Label labelInfo;
    private Button goodbye;
    private Button go;


    public static void main(String[] args) {
        var window = new Main();
        window.setVisible(true);
        window.addKeyListener(window);
    }

    public Main() {
        configureWindow();
        configureCanvas();
        configureUniverse();
        configureNavigation();
        addLightToUniverse();
        configureInitialHashmapOfPositions();
        configureSceneGraph();
        configureFrame();
    }

    private void configureWindow() {
        setTitle("Lab #6");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void configureCanvas() {
        myCanvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        myCanvas3D.setDoubleBufferEnable(true);
        myCanvas3D.addKeyListener(this);
        getContentPane().add(myCanvas3D, BorderLayout.CENTER);
    }

    private void configureUniverse() {
        universe = new SimpleUniverse(myCanvas3D);
        universe.getViewingPlatform().setNominalViewingTransform();
    }

    private void configureNavigation() {
        var ob = new OrbitBehavior(myCanvas3D);
        ob.setReverseRotate(true);
        ob.setSchedulingBounds(new BoundingSphere(new Point3d(), Double.MAX_VALUE));
        universe.getViewingPlatform().setViewPlatformBehavior(ob);
    }

    private void configureFrame() {
        go = new Button("  go  ");
        labelInfo = new Label("To start moving helicopter press `go`. Press `backspace` to reset ");
        goodbye = new Button("goodbye");
        goodbye.setEnabled(false);
        var panel = new Panel();
        panel.add(labelInfo);
        panel.add(go);
        panel.add(goodbye);
        add("North", panel);
        go.addActionListener(this);
        goodbye.addActionListener(this);
    }

    private void addLightToUniverse() {
        var dirLight = new DirectionalLight(
                new Color3f(Color.WHITE),
                new Vector3f(4.0f, -7.0f, -12.0f)
        );
        dirLight.setInfluencingBounds(new BoundingSphere(new Point3d(), 1000));
        root.addChild(dirLight);
    }

    private void configureSceneGraph() {
        addImageInTheSkyBackground();
        var helicopter = getSceneHelicopterFromFile();
        var helicopterTransformGroup = new TransformGroup();

        initTransformGroup = new TransformGroup();
        setInitialTransformation(initTransformGroup);

        var helicopterSceneGroup = helicopter.getSceneGroup();
        helicopterSceneGroup.removeChild((Shape3D)helicopter.getNamedObjects().get("big_propeller"));
        helicopterSceneGroup.removeChild((Shape3D)helicopter.getNamedObjects().get("small_propeller"));

        initTransformGroup.addChild(helicopterSceneGroup);
        initTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        initTransformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);

        helicopterTransformGroup.addChild(initTransformGroup);
        helicopterTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        root.addChild(helicopterTransformGroup);
        addAppearanceForHelicopter(helicopter);

        helicopterSceneGroup.addChild(
                applyRotationForShape(((Shape3D)helicopter.getNamedObjects().get("big_propeller")).cloneTree(),
                transform3D -> transform3D.setTranslation(new Vector3f(0, 0, -0.212f)), 320));

        helicopterSceneGroup.addChild(
                applyRotationForShape(((Shape3D)helicopter.getNamedObjects().get("small_propeller")).cloneTree(),
                transform3D -> {
                    transform3D.rotZ(Math.PI/2);
                    transform3D.setTranslation(new Vector3f(0, 0.061f, 0.845f));
                }, 200));

        root.compile();
        universe.addBranchGraph(root);
    }

    private void configureInitialHashmapOfPositions() {
        helicopter_pos.put("x_loc", new Pair<>(0f, true));
        helicopter_pos.put("y_loc", new Pair<>(0f, true));
        helicopter_pos.put("z_loc", new Pair<>(0f, true));
        helicopter_pos.put("scale", new Pair<>(1f, true));
        helicopter_pos.put("y_axis_rotate", new Pair<>(y_axis_rotate_initial, true));
        helicopter_pos.put("x_axis_rotate", new Pair<>(x_axis_rotate_initial, true));
    }

    private void setInitialTransformation(TransformGroup transformGroup) {
        var transformYAxis = new Transform3D();
        transformYAxis.rotY(y_axis_rotate_initial);
        var transformXAxis = new Transform3D();
        transformXAxis.rotX(x_axis_rotate_initial);
        transformYAxis.mul(transformXAxis);
        transformGroup.setTransform(transformYAxis);
    }

    private Node applyRotationForShape(Node shape, Consumer<Transform3D> transformInitialPosition, int rotateDuration) {
        var transformGroup = new TransformGroup();
        transformGroup.addChild(shape);

        var transform3D = new Transform3D();
        transformInitialPosition.accept(transform3D);

        var alphaRotation = new Alpha(Integer.MAX_VALUE, Alpha.INCREASING_ENABLE,
                0,0, rotateDuration,0,0,
                0, 0,0);

        var rotationInterpolator = new RotationInterpolator(alphaRotation, transformGroup,
                transform3D, (float) Math.PI * 2, 0.0f);

        var bounds = new BoundingSphere(new Point3d(), Double.MAX_VALUE);

        rotationInterpolator.setSchedulingBounds(bounds);
        transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        transformGroup.addChild(rotationInterpolator);

        return transformGroup;
    }

    private void addAppearanceForHelicopter(Scene helicopter) {
        var mainBody = (Shape3D) helicopter.getNamedObjects().get("main_body_");
        var somePartOfBody = (Shape3D) helicopter.getNamedObjects().get("decal");
        addAppearanceForShapes(appearance -> {
            appearance.setTexture(getCamouflageTexture());
            var texAttr = new TextureAttributes();
            texAttr.setTextureMode(TextureAttributes.MODULATE);
            appearance.setTextureAttributes(texAttr);
            var color = new Color3f(new Color(160, 160, 160));
            appearance.setMaterial(new Material(color, color, color, color,10));
        }, mainBody, somePartOfBody);

        var glass = (Shape3D) helicopter.getNamedObjects().get("glass");
        setAppearanceMaterialAsColorForShapes(new Color(186, 201, 203), 300, glass);

        var smallPropeller = (Shape3D) helicopter.getNamedObjects().get("small_propeller");
        var bigPropeller = (Shape3D) helicopter.getNamedObjects().get("big_propeller");
        setAppearanceMaterialAsColorForShapes(new Color(0, 10, 0), 3, smallPropeller, bigPropeller);

        var otherParts = (Shape3D) helicopter.getNamedObjects().get("main_");
        var anotherParts = (Shape3D) helicopter.getNamedObjects().get("alpha");
        setAppearanceMaterialAsColorForShapes(new Color(14, 16, 14), 3, otherParts, anotherParts);

        var rocketHeadings = (Shape3D) helicopter.getNamedObjects().get("missile_gl");
        setAppearanceMaterialAsColorForShapes(new Color(82, 0, 0), 3, rocketHeadings);

        var rockets = (Shape3D) helicopter.getNamedObjects().get("missile_1");
        setAppearanceMaterialAsColorForShapes(new Color(26, 36, 26), 3, rockets);
    }

    private void setAppearanceMaterialAsColorForShapes(Color color, int shininess, Shape3D... shapes) {
        addAppearanceForShapes(appearance -> {
            var colorVector = new Color3f(color);
            appearance.setMaterial(new Material(colorVector, colorVector, colorVector, colorVector, shininess));
        }, shapes);
    }

    private void addAppearanceForShapes(Consumer<Appearance> changeAppearance, Shape3D... shapes) {
        for (var shape : shapes) {
            var appearance = new Appearance();
            changeAppearance.accept(appearance);
            shape.setAppearance(appearance);
        }
    }

    private Scene getSceneHelicopterFromFile() {
        var file = new ObjectFile(ObjectFile.RESIZE);
        file.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
        var inputStream = classLoader.getResourceAsStream(helicopterModelLocation);
        try {
            if (inputStream == null) {
                throw new IOException("Resource " + helicopterModelLocation + " not found");
            }
            return file.load(new BufferedReader(new InputStreamReader(inputStream)));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            return null;
        }
    }

    private TextureLoader getTextureLoader(String path) {
        var textureResource = classLoader.getResource(path);
        if (textureResource == null) {
            System.err.println("Couldn't find texture: " + path);
            System.exit(1);
        }
        return new TextureLoader(textureResource.getPath(), myCanvas3D);
    }

    private Texture getCamouflageTexture() {
        var texture = getTextureLoader(camouflageTextureLocation).getTexture();
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        texture.setBoundaryColor(new Color4f(1, 1, 1, 1));
        return texture;
    }

    private void addImageInTheSkyBackground() {
        var background = new Background(getTextureLoader(backgroundLocation).getImage());
        background.setImageScaleMode(Background.SCALE_FIT_MAX);
        background.setApplicationBounds(new BoundingSphere(new Point3d(), Double.MAX_VALUE));
        background.setCapability(Background.ALLOW_IMAGE_WRITE);
        root.addChild(background);
    }

    private void flyAway() {
        updateHelicopterPosition(() -> {
            var x_loc = helicopter_pos.get("x_loc").getValue0();
            var z_loc = helicopter_pos.get("z_loc").getValue0();
            helicopter_pos.put("z_loc", new Pair<>(z_loc - 0.01f, true));
            helicopter_pos.put("x_loc", new Pair<>(x_loc - 0.003f, true));
        });
    }

    private void moveHelicopter() {
        updateHelicopterPosition(() -> {
            changeValueInRange("x_axis_rotate", -0.2f, 0.2f, 0.001f);
            changeValueInRange("y_axis_rotate", -0.8f, 0.8f, 0.001f);
            changeValueInRange("scale", 0.7f, 1f, 0.001f);
            changeValueInRange("z_loc", -3, 0, 0.005f);
            changeValueInRange("x_loc", -0.1f, 0.1f, 0.0005f);
            changeValueInRange("y_loc", -0.1f, 0.1f, 0.0005f);
        });
    }

    private void updateHelicopterPosition(Runnable func) {
        var x_loc = helicopter_pos.get("x_loc").getValue0();
        var y_loc = helicopter_pos.get("y_loc").getValue0();
        var z_loc = helicopter_pos.get("z_loc").getValue0();
        var scale = helicopter_pos.get("scale").getValue0();
        var y_axis_rotate = helicopter_pos.get("y_axis_rotate").getValue0();
        var x_axis_rotate = helicopter_pos.get("x_axis_rotate").getValue0();

        func.run();

        var transform3D = new Transform3D();
        transform3D.setTranslation(new Vector3f(x_loc, y_loc, z_loc));
        transform3D.setScale(scale);
        var transformYAxis = new Transform3D();
        transformYAxis.rotY(y_axis_rotate);
        var transformXAxis = new Transform3D();
        transformXAxis.rotX(x_axis_rotate);
        transform3D.mul(transformXAxis);
        transform3D.mul(transformYAxis);
        initTransformGroup.setTransform(transform3D);
    }

    private void changeValueInRange(String nameOfValue, float min, float max, float step) {
        var cur = helicopter_pos.get(nameOfValue);
        if (cur.getValue1() && cur.getValue0() < max) {
            helicopter_pos.put(nameOfValue, cur.setAt0(cur.getValue0() + step));
        } else if (cur.getValue1()) {
            helicopter_pos.put(nameOfValue, cur.setAt1(false));
        }
        if (!cur.getValue1() && cur.getValue0() > min) {
            helicopter_pos.put(nameOfValue, cur.setAt0(cur.getValue0() - step));
        } else if (!cur.getValue1()) {
            helicopter_pos.put(nameOfValue, cur.setAt1(true));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == go) {
            if (timer.isRunning()) {
                timer.stop();
                go.setLabel("  go  ");
                labelInfo.setText("To start moving helicopter press `go`. Press `backspace` to reset ");
                setInitialTransformation(initTransformGroup);
                configureInitialHashmapOfPositions();
                goodbye.setEnabled(false);
            } else {
                goodbye.setEnabled(true);
                timer.start();
                go.setLabel("stop");
                labelInfo.setText("To stop moving helicopter press `stop`. Press `backspace` to reset");
            }
        } else if (e.getSource() == goodbye) {
            goodbye.setEnabled(false);
            go.setEnabled(false);
            isFlyAway = true;
        } else if (isFlyAway) {
            flyAway();
        } else {
            moveHelicopter();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            configureInitialHashmapOfPositions();
            setInitialTransformation(initTransformGroup);
            universe.getViewingPlatform().setNominalViewingTransform();
            moveHelicopter();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }
}
