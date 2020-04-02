package ua.kpi.fpm.pzks.maokg;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends Applet implements ActionListener {
    private Button plus = new Button("+");
    private Button minus = new Button(" - ");
    private BranchGroup cubeRubikGroup;
    private final TransformGroup cubeTransformGroup = new TransformGroup();
    private final Transform3D cubeTransform3D = new Transform3D();
    private final Timer timer = new Timer(50, this);
    private double angleY = 0;
    private double angleX = 0;
    private boolean rotateY = true;
    private int dimension = 3;

    public static void main(String[] args) {
        var obj = new Main();
        MainFrame mf = new MainFrame(obj, 700, 700);
        mf.run();
    }

    public Main() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D c = new Canvas3D(config);
        add("Center", c);
        Panel p = new Panel();
        p.add(plus);
        p.add(minus);
        add("North", p);
        plus.addActionListener(this);
        minus.addActionListener(this);

        timer.start();
        SimpleUniverse universe = new SimpleUniverse(c);
        BranchGroup group = createCubeRubikScene();
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(group);
    }

    private BranchGroup createCubeRubikScene() {
        var root = new BranchGroup();

        // add group of scene's objects section start
        cubeTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        cubeTransformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        cubeTransformGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        root.addChild(cubeTransformGroup);
        buildCubeRubikScene();
        // add group of scene's objects section end

        // light section start
        var bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),100);

        var sunLightColor = new Color(200, 255, 253);
        var lightDirect = new DirectionalLight(new Color3f(sunLightColor), new Vector3f(4.0f, -7.0f, -12.0f));
        lightDirect.setInfluencingBounds(bounds);
        root.addChild(lightDirect);

        var ambientLightNode = new AmbientLight(new Color3f(new Color(255, 226, 142)));
        ambientLightNode.setInfluencingBounds(bounds);
        root.addChild(ambientLightNode);
        // light section end

        return root;
    }

    private void buildCubeRubikScene() {
        cubeRubikGroup = new BranchGroup();
        cubeRubikGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        cubeRubikGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        cubeRubikGroup.setCapability(BranchGroup.ALLOW_DETACH);

        float step = 1f / dimension;
        float width = (step / 2f) * 0.85f;
        float start = -0.5f + width;

        for (int z = 0; z < dimension; z++) {
            for (int y = 0; y < dimension; y++) {
                for (int x = 0; x < dimension; x++) {
                    TransformGroup cube = new TransformGroup();

                    var transform = new Transform3D();
                    transform.setTranslation(new Vector3f(start + step * x, start + step * y, start + step * z));
                    cube.setTransform(transform);

                    cube.addChild(buildShape(width));
                    cubeRubikGroup.addChild(cube);
                }
            }
        }
        cubeTransformGroup.addChild(cubeRubikGroup);
    }

    protected Node buildShape(float width) {
        var group = new BranchGroup();
        var box = new Box(width, width, width, Box.GENERATE_TEXTURE_COORDS, null);
        group.addChild(box);

        int[] sidesList = new int[] {
            Box.BACK, Box.TOP, Box.BOTTOM, Box.LEFT, Box.RIGHT, Box.FRONT
        };

        Color[] colorsList = new Color[] {
                Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, Color.WHITE, new Color(255, 75, 0)
        };

        for (int i = 0; i < sidesList.length; i++) {
            Appearance ap = new Appearance();
            ap.setColoringAttributes(new ColoringAttributes(new Color3f(colorsList[i]), ColoringAttributes.SHADE_GOURAUD));
            box.setAppearance(sidesList[i], ap);
        }

        return group;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == plus) {
            if (dimension > 13) {
                return;
            }
            dimension++;
            cubeTransformGroup.removeChild(cubeRubikGroup);
            buildCubeRubikScene();
        } else if (e.getSource() == minus) {
            if (dimension < 4) {
                return;
            }
            dimension--;
            cubeTransformGroup.removeChild(cubeRubikGroup);
            buildCubeRubikScene();
        } else {
            if (rotateY) {
                cubeTransform3D.rotY(angleY);
                angleY += 0.05;
                if (angleY >= 25) {
                    rotateY = !rotateY;
                    angleY = 0;
                }
            } else {
                cubeTransform3D.rotX(angleX);
                angleX += 0.05;
                if (angleX >= 25) {
                    rotateY = !rotateY;
                    angleX = 0;
                }
            }
            cubeTransformGroup.setTransform(cubeTransform3D);
        }
    }
}