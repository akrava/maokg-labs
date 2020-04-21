package ua.kpi.fpm.pzks.maokg;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

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
import java.util.Map;

public class Main extends JFrame implements ActionListener, KeyListener {
    private final static String tramModelLocation = "tram/tram.obj";
    private final static String tramTextureLocation = "tram/tram.png";
    private final static String stationBackgroundLocation = "backgrounds/polova.jpg";
    private final static String depotBackgroundLocation = "backgrounds/depot.jpg";
    private final BranchGroup root = new BranchGroup();
    private final Canvas3D canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
    private final TransformGroup wholeTram = new TransformGroup();
    private final Transform3D transform3D = new Transform3D();
    private final Transform3D rotateTransformX = new Transform3D();
    private final Transform3D rotateTransformY = new Transform3D();
    private final Transform3D rotateTransformZ = new Transform3D();
    private final Button go = new Button();
    private final Label labelInfo = new Label();
    private final Timer timer = new Timer(20, this);
    private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private SimpleUniverse universe;
    private Scene tram;
    private Map<String, Shape3D> nameMap;

    private float x_eye_loc = -3.985f;
    private float y_eye_loc = -0.07f;
    private float z_eye_loc = 1.635f;

    private float angle_eye = 0;
    private boolean in_depot = true;

    private Background background;

    public static void main(String[] args) {
        try {
            var window = new Main();
            window.addKeyListener(window);
            window.setVisible(true);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public Main() throws IOException {
        configureWindow();
        configureCanvas();
        configureUniverse();
        addModelToUniverse();
        setTramElementsList();
        addAppearance();
        addImageBackground(depotBackgroundLocation);
        addLightToUniverse();
        addOtherLight();
        changeViewAngle();
        root.compile();
        universe.addBranchGraph(root);
        configureFrame();
    }

    private void configureWindow() {
        setTitle("Lab #5");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void configureCanvas() {
        canvas.setDoubleBufferEnable(true);
        getContentPane().add(canvas, BorderLayout.CENTER);
    }

    private void configureUniverse() {
        universe = new SimpleUniverse(canvas);
        universe.getViewingPlatform().setNominalViewingTransform();
    }

    private void configureFrame() {
        configurePanelInDepot();
        canvas.addKeyListener(this);
        var panel = new Panel();
        panel.add(labelInfo);
        panel.add(go);
        add("North", panel);
        go.addActionListener(this);
        go.addKeyListener(this);
    }

    private void configurePanelInDepot() {
        labelInfo.setText("Rotate eye view with `←` and `→`. " +
                "Rotate tram around the x, y, z axis with `x`, `y`, `z`. " +
                "`Backspace` to set default");
        go.setLabel("Go on a route");
    }

    private void addModelToUniverse() throws IOException {
        tram = getSceneFromFile(tramModelLocation);
    }

    private void addLightToUniverse() {
        var dirLight = new DirectionalLight(
                new Color3f(Color.WHITE),
                new Vector3f(4.0f, -7.0f, -12.0f)
        );
        dirLight.setInfluencingBounds(new BoundingSphere(new Point3d(), 1000));
        root.addChild(dirLight);
    }

    private void setInitialTransformInDepot() {
        transform3D.setIdentity();
        transform3D.rotY(Math.PI);
        transform3D.setScale(2);
        wholeTram.setTransform(transform3D);
    }

    private void setTramElementsList() {
        nameMap = tram.getNamedObjects();
        setInitialTransformInDepot();
        wholeTram.addChild(tram.getSceneGroup());
        wholeTram.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        root.addChild(wholeTram);
    }

    private TextureLoader getTextureLoader(String path) throws IOException {
        var textureResource = classLoader.getResource(path);
        if (textureResource == null) {
            throw new IOException("Couldn't find texture: " + path);
        }
        return new TextureLoader(textureResource.getPath(), canvas);
    }

    private Texture getTexture(String path) throws IOException {
        var texture = getTextureLoader(path).getTexture();
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        texture.setBoundaryColor(new Color4f(1.0f, 1.0f, 1.0f, 1.0f ));
        return texture;
    }

    private Material getMaterial() {
        var material = new Material();
        material.setAmbientColor(new Color3f(new Color(243, 242, 221)));
        material.setDiffuseColor(new Color3f(new Color(255, 233, 207)));
        material.setSpecularColor(new Color3f(new Color(255, 203, 195)));
        material.setLightingEnable(true);
        return material;
    }

    private void addAppearance() throws IOException {
        var tramAppearance = new Appearance();
        tramAppearance.setTexture(getTexture(tramTextureLocation));
        var texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        tramAppearance.setTextureAttributes(texAttr);
        tramAppearance.setMaterial(getMaterial());
        var plane = nameMap.get("tram");
        plane.setAppearance(tramAppearance);
    }

    private void addImageBackground(String path) throws IOException {
        background = new Background(getTextureLoader(path).getImage());
        background.setImageScaleMode(Background.SCALE_FIT_MAX);
        background.setApplicationBounds(new BoundingSphere(new Point3d(),1000));
        background.setCapability(Background.ALLOW_IMAGE_WRITE);
        root.addChild(background);
    }

    private void changeViewAngle() {
        changeViewAngle(0);
    }

    private void setDepotEyeLocation() {
        x_eye_loc = -3.985f;
        y_eye_loc = -0.07f;
        z_eye_loc = 1.635f;
    }

    private void changeViewAngle(float angle) {
        var vp = universe.getViewingPlatform();
        var transform = new Transform3D();
        transform.lookAt(
                new Point3d(x_eye_loc * Math.cos(angle), y_eye_loc, z_eye_loc + x_eye_loc * Math.sin(angle)),
                new Point3d(-0.7, 0, 0),
                new Vector3d(0, 1, 0)
        );
        transform.invert();
        vp.getViewPlatformTransform().setTransform(transform);
    }

    private void addOtherLight() {
        var ambientLight = new AmbientLight(new Color3f(Color.WHITE));
        var directionalLight = new DirectionalLight(
                new Color3f(Color.BLACK),
                new Vector3f(-1F, -1F, -1F)
        );
        var influenceRegion = new BoundingSphere(new Point3d(), 1000);
        ambientLight.setInfluencingBounds(influenceRegion);
        directionalLight.setInfluencingBounds(influenceRegion);
        root.addChild(ambientLight);
        root.addChild(directionalLight);
    }

    private Scene getSceneFromFile(String location) throws IOException {
        ObjectFile file = new ObjectFile(ObjectFile.RESIZE);
        file.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
        var inputStream = classLoader.getResourceAsStream(location);
        if (inputStream == null) {
            throw new IOException("Resource " + location + " not found");
        }
        return file.load(new BufferedReader(new InputStreamReader(inputStream)));
    }

    private float x_temp = 0;
    private float y_temp = 0;

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        float diff = 0.05f;
        if (in_depot) {
            switch (keyCode) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_RIGHT: {
                    if (keyCode == KeyEvent.VK_RIGHT) {
                        diff *= -1;
                    }
                    angle_eye += diff;
                    changeViewAngle(angle_eye);
                } break;
                case KeyEvent.VK_X:
                case KeyEvent.VK_Y:
                case KeyEvent.VK_Z: {
                    if (keyCode == KeyEvent.VK_X) {
                        rotateTransformX.rotX(diff);
                        transform3D.mul(rotateTransformX);
                    } else if (keyCode == KeyEvent.VK_Y) {
                        rotateTransformY.rotY(diff);
                        transform3D.mul(rotateTransformY);
                    } else {
                        rotateTransformZ.rotZ(diff);
                        transform3D.mul(rotateTransformZ);
                    }
                    wholeTram.setTransform(transform3D);
                } break;
                case KeyEvent.VK_BACK_SPACE: {
                    rotateTransformX.setIdentity();
                    rotateTransformY.setIdentity();
                    rotateTransformZ.setIdentity();
                    transform3D.setIdentity();
                    setInitialTransformInDepot();
                    angle_eye = 0;
                    changeViewAngle(angle_eye);
                }
            }
        } else {
            if (e.getKeyChar() == '1') {
                x_eye_loc += 0.01;
            }
            if (e.getKeyChar() == '2') {
                x_eye_loc -= 0.01;
            }
            if (e.getKeyChar() == '3') {
                y_eye_loc += 0.01;
            }
            if (e.getKeyChar() == '4') {
                y_eye_loc -= 0.01;
            }
            if (e.getKeyChar() == '5') {
                z_eye_loc += 0.01;
            }
            if (e.getKeyChar() == '6') {
                z_eye_loc -= 0.01;
            }
            changeViewAngle();
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                y_temp += 0.01;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                y_temp -= 0.01;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                x_temp += 0.01;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                x_temp -= 0.01;
            }

            transform3D.setTranslation(new Vector3f(x_temp, 0, y_temp));

           // wholeTram.setTransform(transform3D);

            if (e.getKeyCode() == KeyEvent.VK_SPACE && !in_depot) {
                if (timer.isRunning()) {
                    timer.stop();
                } else {
                    timer.start();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }

    private boolean y = false;
    float y_loc_cur = -0.7f;
    int count_ = 1;

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == go) {
            if (in_depot && !timer.isRunning()) {
                timer.start();
                go.setEnabled(false);
                //go.setLabel("Stop");
            } else {
                timer.stop();
                //go.setLabel("Go");
            }
        } else {
            if (in_depot) {
                moveTramToGoOnRoute();
                return;
            } else {
                //transform3D.setIdentity();
                transform3D.setTranslation(new Vector3f(8.04f - x_loc_cur, -0.03f + -0.66f, -0.5f + y_loc_cur));
                transform3D.setScale(scale_cur);
                wholeTram.setTransform(transform3D);
                if (!y) {
                    x_loc_cur += 0.005 + 0.003 * Math.pow(1.01, count_);
                    scale_cur += 0.025;
                    y_loc_cur += 0.015;
                    count_++;
                }
                if (x_loc_cur > 30) {
                    x_loc_cur = 0;
                    scale_cur = 0.7f;
                    y_loc_cur = -0.7f;
                    count_ = 1;
                }
                //y = true;
            }
        }
    }

    private float y_angle = (float) Math.PI;
    private float x_loc_cur = 0;
    private float scale_cur = 2;

    private void moveTramToGoOnRoute() {
        if (y_angle > 0) {
            rotateTransformY.rotY(-0.01);
            transform3D.mul(rotateTransformY);
            wholeTram.setTransform(transform3D);
            y_angle -= 0.01;
        } else if (scale_cur > 1.5) {
            transform3D.setTranslation(new Vector3f(x_loc_cur, 0, 0));
            transform3D.setScale(scale_cur);
            wholeTram.setTransform(transform3D);
            x_loc_cur += 0.01;
            scale_cur -= 0.001;
        } else {
            in_depot = false;
            timer.stop();
            prepareSceneForStation();
            timer.start();
            x_loc_cur = 0;
            scale_cur = 0.7f;
        }
    }

    private void prepareSceneForStation() {
        try {
            background.setImage(getTextureLoader(stationBackgroundLocation).getImage());
            setInitialTransformInDepot();
            transform3D.setTranslation(new Vector3f(-0.06f, 0, 0.12f));
            wholeTram.setTransform(transform3D);
            x_eye_loc = -9.835f;
            y_eye_loc = -0.09f;
            z_eye_loc = -2.945f;
            changeViewAngle(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
