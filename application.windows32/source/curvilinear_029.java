import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import g4p_controls.*; 
import processing.pdf.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class curvilinear_029 extends PApplet {

/* //<>//
Copyright 2019 Studio RGL LLP

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

// We don't require you to credit us, but if you use it in your work feel free to point people to our twitter
// https://twitter.com/RealGoodLiars

// animation curvilinear grid generator
// works over here, if u got bugs, sorry, ask a real programmer cos we just hacked this together :-)
// hope it helps tho!

// usage:
// 1) run the program (tested using Processing 3.5, you can compile and run standalone if u want)
// 2) pick a file location
// 3) move the mouse to get the grid you want
// 4) click to save a pdf
// 5) done I guess, make some cool animation!






// control values
int control_curvePrecision = 48;
int control_projectionMode = 0;

float   control_yRotation;
float   control_xRotation;
PVector control_boxSize;
PVector control_viewCentre;
float   control_gridFrequency;




File pdfPath;
boolean waitingToSave = false;
boolean saving = false;

public void setup() {
  
    //this just runs the program in interactive mode
  loop();

  createGUI();
}


public void draw() {
  clear();

  // Read GUI
  control_yRotation = radians(slider_yRotation.getValueF());//slider_control_yRotation;//radians(180+360*mouseX/width); //radians(frameCount*10-1);//radians(45);//radians(frameCount-1);
  control_xRotation = radians(slider_xRotation.getValueF());
  control_boxSize = new PVector (slider_scale_X.getValueF(), slider_scale_Y.getValueF(), slider_scale_Z.getValueF());
  control_viewCentre = new PVector (-slider_XZ.getValueXF(), -slider_height.getValueF(), slider_XZ.getValueYF());
  control_viewCentre = new PVector(control_viewCentre.x * control_boxSize.x, control_viewCentre.y * control_boxSize.y, control_viewCentre.z * control_boxSize.z); 
  control_gridFrequency = slider_gridFrequency.getValueF();
  control_curvePrecision = slider_curvePrecision.getValueI();
  
  
  
  
  String pdfLocation = "";
  
  if (pdfPath != null)
  {
    pdfLocation = pdfPath.getAbsolutePath() + ".pdf";
    if (waitingToSave)
    {
      println ("waitingToSave");
      println (pdfLocation);
      beginRecord(PDF, pdfLocation);
      waitingToSave = false;
      saving = true;
    }
  }
  else
  {
    if (waitingToSave){
      println("PDF path is null but we're tryna save");
    }
  }
  
  
  
 
 
 
  background(255);
  
  
  fill(34);
  text(getDataString(), 32,32);
  noFill();
  
  translate(width/2,height/2);

  
  // now we gonna do all the drawin'
  
   PVector x = new PVector(1,0,0);
   PVector y = new PVector(0,1,0);
   PVector z = new PVector(0,0,1);
   PVector transformedViewCentre = control_viewCentre.copy();
   
   x = rotateAroundY(x, control_yRotation);
   y = rotateAroundY(y, control_yRotation); // superfluous really?
   z = rotateAroundY(z, control_yRotation);
   transformedViewCentre = rotateAroundY(transformedViewCentre, control_yRotation);
   
   // not yet implemented
   //println("x rotation = " + control_xRotation);
   x = rotateAroundX(x, control_xRotation);
   y = rotateAroundX(y, control_xRotation);
   z = rotateAroundX(z, control_xRotation);
   transformedViewCentre = rotateAroundX(transformedViewCentre, control_xRotation);
     
   
  //direction,  perpendicular,  centre,  width,  length, int nLines)
 
  // X PLANES
  stroke(255, 0, 0);
  drawPlane(y,z, PVector.add(transformedViewCentre, x.copy().mult(0.5f * control_boxSize.x)), control_boxSize.z,control_boxSize.y, control_gridFrequency);
  drawPlane(y,z, PVector.sub(transformedViewCentre, x.copy().mult(0.5f * control_boxSize.x)), control_boxSize.z,control_boxSize.y, control_gridFrequency);
  
  // Z PLANE
  stroke(0, 127, 255);
  drawPlane(x,y, PVector.add(transformedViewCentre, z.copy().mult(0.5f * control_boxSize.z)), control_boxSize.y,control_boxSize.x, control_gridFrequency);

  // green for y
  stroke (0,255,0);
  drawPlane(x,z, PVector.sub(transformedViewCentre, y.copy().mult(0.5f * control_boxSize.y)), control_boxSize.z,control_boxSize.x, control_gridFrequency);
  stroke (0, 127,0);
  drawPlane(x,z, PVector.add(transformedViewCentre, y.copy().mult(0.5f * control_boxSize.y)), control_boxSize.z,control_boxSize.x, control_gridFrequency); // ground
  
  
  
  // vanishing points
  stroke(0);
  float vpDistance = 400;
  float crossSize = vpDistance/8;
  drawCross(y, x, PVector.add(transformedViewCentre, z.copy().mult(vpDistance)), crossSize);
  drawCross(y, x, PVector.sub(transformedViewCentre, z.copy().mult(vpDistance)), crossSize);
  drawCross(y, z, PVector.add(transformedViewCentre, x.copy().mult(vpDistance)), crossSize);
  drawCross(y, z, PVector.sub(transformedViewCentre, x.copy().mult(vpDistance)), crossSize);
  drawCross(x, z, PVector.sub(transformedViewCentre, y.copy().mult(vpDistance)), crossSize);
  drawCross(x, z, PVector.sub(transformedViewCentre, y.copy().mult(vpDistance)), crossSize);
  
  
  
  // stop recording if we were recording
  if (saving == true)
  {
    endRecord();
    saving = false;
    print ("Save completed");
  }
  
  
  translate(-width/2, -height/2);
}


public void drawCross(PVector direction, PVector perpendicular, PVector centre, float crossSize)
{
  
  PVector left = centre.copy();
  PVector bottom = centre.copy();
  PVector centreToLeft = perpendicular.copy().mult(-crossSize/2);
  PVector centreToBottom = direction.copy().mult(-crossSize/2);
  left.add(centreToLeft);
  bottom.add(centreToBottom);
  
  
  //println ("1 left = " + left);
  //println ("1 bottom = " + bottom);
  //println ("1 direction = " + direction);
  //println ("1 perpendicular = " + perpendicular);
  //println ("1 size = " + crossSize);
 
  // draw cross
  drawLine(perpendicular, left, crossSize);
  drawLine(direction, bottom, crossSize);
  
 
  
}

public void drawPlane(PVector direction, PVector perpendicular, PVector centre, float planeWidth, float planeLength, float lineFrequency)
{
  direction.normalize();
  perpendicular.normalize();
  
  int MAX_LINES = 200;
  int nLinesX = max(3, min(MAX_LINES, PApplet.parseInt(lineFrequency*planeWidth)));
  int nLinesY = max(3, min(MAX_LINES, PApplet.parseInt(lineFrequency*planeLength)));
  
  
  drawLineArray(direction, perpendicular, centre, planeWidth, planeLength, nLinesX);
  drawLineArray(perpendicular, direction, centre, planeLength, planeWidth, nLinesY);
}



public void drawLineArray(PVector direction, PVector perpendicular, PVector centre, float arrayWidth, float lineLength, int nLines){
  //PVector perpendicular = normal.cross(direction);
  //println ("normal = " + normal);
  // println ("1 perpendicular = " + perpendicular);
  // println ("1 direction = " + direction);
  // println ("1 planeWidth = " + arrayWidth);
  // println ("1 lineLength = " + lineLength);
  
  PVector centreToEdge = new PVector();
  PVector centreToBottom = new PVector();
  PVector bottomCentre = new PVector();
  PVector bottomCorner = new PVector();
  PVector lineStep  = new PVector();
  
  PVector.mult(perpendicular, -0.5f*arrayWidth, centreToEdge); // calculate vector from centre to edge
  PVector.mult(direction, -0.5f*lineLength, centreToBottom); // calculate vector from centre to bottom
  PVector.add(centre, centreToBottom, bottomCentre); // calculate where the bottom centre is
  PVector.add(bottomCentre, centreToEdge, bottomCorner); // calculate where the bottom corner is
  PVector.mult(perpendicular, arrayWidth, lineStep);
  
  for (int iLine = 0; iLine < nLines; iLine++)
  {
    PVector lineStart = lineStep.copy();
    PVector lineDirection = direction.copy();
 
    lineStart.mult(PApplet.parseFloat(iLine)/(nLines-1)); // that's the step, if there's 5 lines there's 4 spaces
    lineStart.add(bottomCorner);
    
    drawLine(lineDirection, lineStart, lineLength);
        
  }
}




// subroutine that draws a line curvilinearly (sp?)
public void drawLine(PVector step, PVector start, float desiredLineLength){
  int nPoints = control_curvePrecision;
  
  if (nPoints < 2)
  {
    return;
  }
  
  step = step.normalize();
  step.mult(desiredLineLength/(nPoints-1));
  
  // draw x lines
  float x = start.x;
  float y = start.y;
  float z = start.z;

  
  float[] xPoint = new float [nPoints];
  float[] yPoint = new float [nPoints];
  boolean[] validPoint = new boolean[nPoints];
  
  for (int iPoint = 0; iPoint < nPoints; iPoint++){
      x = start.x + iPoint * step.x;
      y = start.y + iPoint * step.y;
      z = start.z + iPoint * step.z;


      PVector v1 = getCoordinates(x,y,z);

      xPoint[iPoint] = v1.x;
      yPoint[iPoint] = v1.y;
      
      validPoint[iPoint] = z>0;   //don't render points behind the camera...
     
    }
  
  
    boolean drawing = false;
    for (int i = 0; i<nPoints; i++)
    {
      if (validPoint[i])
      {
        if (drawing == false) // if we weren't drawing, but this point is good, let's do it 
        {
          drawing = true;
          beginShape();
        }
        curveVertex(xPoint[i],  yPoint[i]);
      }
      else
      {
        if (drawing)
        {
          // if we were drawing, but it's time to stop (because the point isn't valid), let's stop
          endShape();
          drawing = false;
        }
      }
    }
    if (drawing)
    {
      endShape();
    }
}

// just rotates a vector
public PVector rotateAroundY( PVector source, float angle){
  PVector xz = new PVector (source.x, source.z);
  xz = xz.rotate(angle);
  return new PVector(xz.x, source.y, xz.y);
}

// just rotates a vector
public PVector rotateAroundX( PVector source, float angle){
  PVector yz = new PVector (source.y, source.z);
  yz = yz.rotate(angle);
  return new PVector(source.x, yz.x, yz.y);
}


//gets coordinates of a xyz point in 2D space
public PVector getCoordinates(float x, float y, float z){
  float alpha = 0;
  float beta = 0;
  float scale = width/3.5f;//float(mouseY)/height;
  
  //old fisheye version
  //alpha = atan2(x, sqrt(y*y + z*z)) * scale;
  //beta = atan2(y, sqrt(x*x + z*z))  * scale;
  
  
  // hemispheric polar
  if (control_projectionMode == 0)
  {
    alpha = 0.5f*width*x/(sqrt(x*x + y*y + z*z));
    beta = 0.5f*width*y/(sqrt(x*x + y*y + z*z));
  }
  else if (control_projectionMode == 1)
  {
    // cylindrical
    alpha = atan2(x,z)*width   *0.25f;
    beta = 0.5f*width*y/(sqrt(x*x + y*y + z*z)) * 0.5f;
  }
  
  return new PVector(alpha, beta, 0);
}





// from  https://processing.org/reference/selectOutput_.html
public void fileSelected(File selection) {
  if (selection == null)
  {
    //println("Window was closed or the user hit cancel.");
    pdfPath = null;
    waitingToSave = false;
  }
  else
  {
    pdfPath = selection;
    waitingToSave = true;
    println("waitingToSave: " + selection.getAbsolutePath());
  }
}


// just gets all the data
public String getDataString()
{
  String answer = "Studio RGL Curvilinear Grid Generator v0.22";
  answer += "\nwww.twitter.com/RealGoodLiars";
  answer += "\nBuilt with Processing and G4P GUI";
  answer += "\nBox Scale: " + control_boxSize;
  answer += "\nView Centre: " + control_viewCentre;
  answer += "\nRotation XY: " + control_xRotation + ", " + control_yRotation;
  
  return answer;
}

public void triggerSave()
{
  selectOutput("Select a file to write to:", "fileSelected");
}

// save a pdf if we press the mouse
public void mousePressed(){
  // triggerSave();
}
/* =========================================================
 * ====                   WARNING                        ===
 * =========================================================
 * The code in this tab has been generated from the GUI form
 * designer and care should be taken when editing this file.
 * Only add/edit code inside the event handlers i.e. only
 * use lines between the matching comment tags. e.g.

 void myBtnEvents(GButton button) { //_CODE_:button1:12356:
     // It is safe to enter your event code here  
 } //_CODE_:button1:12356:
 
 * Do not rename this tab!
 * =========================================================
 */

public void button_save_click1(GButton source, GEvent event) { //_CODE_:button_save:216087:
  triggerSave();
  //println("button2 - GButton >> GEvent." + event + " @ " + millis());
} //_CODE_:button_save:216087:

public void slider_yRotation_change(GSlider source, GEvent event) { //_CODE_:slider_yRotation:559070:
  //println("slider_yRotation - GSlider >> GEvent." + event + " @ " + millis());
} //_CODE_:slider_yRotation:559070:

public void slider_XZ_change1(GSlider2D source, GEvent event) { //_CODE_:slider_XZ:336042:
  //println("slider_XZ - GSlider2D >> GEvent." + event + " @ " + millis());
} //_CODE_:slider_XZ:336042:

public void slider_height_change1(GSlider source, GEvent event) { //_CODE_:slider_height:889538:
  //println("slider_height - GSlider >> GEvent." + event + " @ " + millis());
} //_CODE_:slider_height:889538:

public void slider1_change1(GSlider source, GEvent event) { //_CODE_:slider_scale_X:487261:
  //println("slider_scale_X - GSlider >> GEvent." + event + " @ " + millis());
} //_CODE_:slider_scale_X:487261:

public void slider2_change1(GSlider source, GEvent event) { //_CODE_:slider_scale_Y:318543:
  //println("slider_scale_Y - GSlider >> GEvent." + event + " @ " + millis());
} //_CODE_:slider_scale_Y:318543:

public void slider3_change1(GSlider source, GEvent event) { //_CODE_:slider_scale_Z:746219:
  //println("slider_scale_Z - GSlider >> GEvent." + event + " @ " + millis());
} //_CODE_:slider_scale_Z:746219:

public void slider_gridFrequency_change(GSlider source, GEvent event) { //_CODE_:slider_gridFrequency:959643:
  //println("slider_lineFrequency - GSlider >> GEvent." + event + " @ " + millis());
} //_CODE_:slider_gridFrequency:959643:

public void slider1_change2(GSlider source, GEvent event) { //_CODE_:slider_curvePrecision:725681:
  //println("slider_curvePrecision - GSlider >> GEvent." + event + " @ " + millis());
} //_CODE_:slider_curvePrecision:725681:

public void option1_clicked1(GOption source, GEvent event) { //_CODE_:option1:301875:
  //println("option1 - GOption >> GEvent." + event + " @ " + millis());
  control_projectionMode = 0;
} //_CODE_:option1:301875:

public void option2_clicked1(GOption source, GEvent event) { //_CODE_:option2:454197:
  //println("option2 - GOption >> GEvent." + event + " @ " + millis());
  control_projectionMode = 1;
} //_CODE_:option2:454197:

public void slider1_change3(GSlider source, GEvent event) { //_CODE_:slider_xRotation:469416:
  //println("slider_xRotation - GSlider >> GEvent." + event + " @ " + millis());
} //_CODE_:slider_xRotation:469416:



// Create all the GUI controls. 
// autogenerated do not edit
public void createGUI(){
  G4P.messagesEnabled(false);
  G4P.setGlobalColorScheme(GCScheme.BLUE_SCHEME);
  G4P.setMouseOverEnabled(false);
  surface.setTitle("Sketch Window");
  button_save = new GButton(this, 928, 976, 80, 30);
  button_save.setText("Save PDF");
  button_save.addEventHandler(this, "button_save_click1");
  slider_yRotation = new GSlider(this, 112, 992, 256, 16, 10.0f);
  slider_yRotation.setShowValue(true);
  slider_yRotation.setLimits(0.0f, -180.0f, 180.0f);
  slider_yRotation.setNumberFormat(G4P.DECIMAL, 2);
  slider_yRotation.setOpaque(false);
  slider_yRotation.addEventHandler(this, "slider_yRotation_change");
  label_yRotation = new GLabel(this, 16, 992, 80, 16);
  label_yRotation.setText("Y rotation");
  label_yRotation.setOpaque(false);
  slider_XZ = new GSlider2D(this, 432, 848, 160, 160);
  slider_XZ.setLimitsX(0.0f, -0.5f, 0.5f);
  slider_XZ.setLimitsY(0.4f, -0.5f, 0.5f);
  slider_XZ.setNumberFormat(G4P.DECIMAL, 2);
  slider_XZ.setOpaque(false);
  slider_XZ.addEventHandler(this, "slider_XZ_change1");
  label_position = new GLabel(this, 432, 832, 128, 16);
  label_position.setText("Position");
  label_position.setOpaque(false);
  slider_height = new GSlider(this, 624, 832, 176, 16, 10.0f);
  slider_height.setRotation(PI/2, GControlMode.CORNER);
  slider_height.setLimits(0.0f, -1.0f, 1.0f);
  slider_height.setNumberFormat(G4P.DECIMAL, 2);
  slider_height.setOpaque(false);
  slider_height.addEventHandler(this, "slider_height_change1");
  slider_scale_X = new GSlider(this, 112, 896, 256, 16, 10.0f);
  slider_scale_X.setLimits(1.0f, 0.1f, 10.0f);
  slider_scale_X.setNumberFormat(G4P.DECIMAL, 2);
  slider_scale_X.setOpaque(false);
  slider_scale_X.addEventHandler(this, "slider1_change1");
  slider_scale_Y = new GSlider(this, 112, 928, 256, 16, 10.0f);
  slider_scale_Y.setLimits(1.0f, 0.1f, 10.0f);
  slider_scale_Y.setNumberFormat(G4P.DECIMAL, 2);
  slider_scale_Y.setOpaque(false);
  slider_scale_Y.addEventHandler(this, "slider2_change1");
  slider_scale_Z = new GSlider(this, 112, 960, 256, 16, 10.0f);
  slider_scale_Z.setLimits(1.0f, 0.1f, 10.0f);
  slider_scale_Z.setNumberFormat(G4P.DECIMAL, 2);
  slider_scale_Z.setOpaque(false);
  slider_scale_Z.addEventHandler(this, "slider3_change1");
  label4 = new GLabel(this, 16, 896, 80, 16);
  label4.setText("Box Scale X");
  label4.setOpaque(false);
  slider_gridFrequency = new GSlider(this, 112, 832, 256, 16, 10.0f);
  slider_gridFrequency.setLimits(10.0f, 5.0f, 30.0f);
  slider_gridFrequency.setNumberFormat(G4P.DECIMAL, 2);
  slider_gridFrequency.setOpaque(false);
  slider_gridFrequency.addEventHandler(this, "slider_gridFrequency_change");
  label5 = new GLabel(this, 16, 928, 80, 16);
  label5.setText("Box Scale Y");
  label5.setOpaque(false);
  label6 = new GLabel(this, 16, 960, 80, 20);
  label6.setText("Box Scale Z");
  label6.setOpaque(false);
  label8 = new GLabel(this, 16, 864, 80, 16);
  label8.setText("Precision");
  label8.setOpaque(false);
  label7 = new GLabel(this, 16, 832, 80, 20);
  label7.setText("Grid Freq.");
  label7.setOpaque(false);
  slider_curvePrecision = new GSlider(this, 112, 864, 256, 16, 10.0f);
  slider_curvePrecision.setLimits(50.0f, 10.0f, 200.0f);
  slider_curvePrecision.setNumberFormat(G4P.DECIMAL, 2);
  slider_curvePrecision.setOpaque(false);
  slider_curvePrecision.addEventHandler(this, "slider1_change2");
  toggle_projectionMode = new GToggleGroup();
  option1 = new GOption(this, 640, 960, 120, 16);
  option1.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
  option1.setText("Polar Hemispheric");
  option1.setOpaque(false);
  option1.addEventHandler(this, "option1_clicked1");
  option2 = new GOption(this, 640, 992, 120, 16);
  option2.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
  option2.setText("Cylindrical");
  option2.setOpaque(false);
  option2.addEventHandler(this, "option2_clicked1");
  toggle_projectionMode.addControl(option1);
  option1.setSelected(true);
  toggle_projectionMode.addControl(option2);
  slider_xRotation = new GSlider(this, 400, 832, 176, 16, 10.0f);
  slider_xRotation.setShowValue(true);
  slider_xRotation.setRotation(PI/2, GControlMode.CORNER);
  slider_xRotation.setLimits(0.0f, -90.0f, 90.0f);
  slider_xRotation.setNumberFormat(G4P.DECIMAL, 2);
  slider_xRotation.setOpaque(false);
  slider_xRotation.addEventHandler(this, "slider1_change3");
}

// Variable declarations 
// autogenerated do not edit
GButton button_save; 
GSlider slider_yRotation; 
GLabel label_yRotation; 
GSlider2D slider_XZ; 
GLabel label_position; 
GSlider slider_height; 
GSlider slider_scale_X; 
GSlider slider_scale_Y; 
GSlider slider_scale_Z; 
GLabel label4; 
GSlider slider_gridFrequency; 
GLabel label5; 
GLabel label6; 
GLabel label8; 
GLabel label7; 
GSlider slider_curvePrecision; 
GToggleGroup toggle_projectionMode; 
GOption option1; 
GOption option2; 
GSlider slider_xRotation; 

  public void settings() {  size(1024, 1024); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "curvilinear_029" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
