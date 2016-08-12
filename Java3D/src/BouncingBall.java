import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.Timer;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class BouncingBall extends Applet implements ActionListener {

	private static final long serialVersionUID = 1L;

	private TransformGroup objTrans;
	private Transform3D trans = new Transform3D();
	private float height = 0.0f;
	private float sign = 1.0f;
	private Timer timer;
	private float xloc = 0.0f;
	
	public BranchGroup createSceneGraph() {
		BranchGroup objRoot = new BranchGroup();
		objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objRoot.addChild(objTrans);
		Sphere sphere = new Sphere(0.25f);
		objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		Transform3D pos1 = new Transform3D();
		pos1.setTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
		objTrans.setTransform(pos1);
		objTrans.addChild(sphere);
		objRoot.addChild(objTrans);
		BoundingSphere bounds =	new BoundingSphere(
			new Point3d(0.0, 0.0, 0.0),
			100.0
		);
		DirectionalLight light1	= new DirectionalLight(
			new Color3f(1.0f, 1.0f, 0.6f),
			new Vector3f(4.0f, -7.0f, -12.0f)
		);
		light1.setInfluencingBounds(bounds);
		objRoot.addChild(light1);
		Color3f ambientColor = new Color3f(1.0f, 0.8f, 0.8f);
		AmbientLight ambientLightNode = new AmbientLight(ambientColor);
		ambientLightNode.setInfluencingBounds(bounds);
		objRoot.addChild(ambientLightNode);
		return objRoot;
	}
	
	public BouncingBall() {
		setLayout(new BorderLayout());
		GraphicsConfiguration config =
				SimpleUniverse.getPreferredConfiguration();
		Canvas3D c = new Canvas3D(config);
		add("Center", c);
		timer = new Timer(100, this);
		if (!timer.isRunning()) {
			timer.start();
		}
		Panel p = new Panel();
		add("North", p);
		BranchGroup scene = createSceneGraph();
		SimpleUniverse u = new SimpleUniverse(c);
		u.getViewingPlatform().setNominalViewingTransform();
		u.addBranchGraph(scene);
	}
	
	@Override
	public void actionPerformed(ActionEvent e ) {
		height += .1 * sign;
		if (Math.abs(height *2) >= 1 )
			sign = -1.0f * sign;
		if (height<-0.4f) {
			trans.setScale(new Vector3d(1.0, .8, 1.0));
		} else {
			trans.setScale(new Vector3d(1.0, 1.0, 1.0));
		}
		trans.setTranslation(new Vector3f(xloc,height,0.0f));
		objTrans.setTransform(trans);
	}	
	
	public static void main(String[] args) {
		System.out.println("Program Started");
		BouncingBall bb = new BouncingBall();
		new MainFrame(bb, 512, 512);
	}
}