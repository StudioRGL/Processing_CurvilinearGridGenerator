import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 
import processing.pdf.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class curvilinearGridGenerator extends PApplet {

/*
Copyright 2019 Studio RGL LLP

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

  // switched to a different GUI library



// control values
int control_curvePrecision = 64;
//int control_projectionMode = 0;

ControlP5 cp5;
ControlWindow controlWindow;

float   control_yRotation = 0;
float   control_xRotation = 0;
PVector control_boxSize = new PVector(1,1,1);
PVector control_viewCentre = new PVector(0,0,0);
float   control_gridFrequency = 16;
float   control_xBoxSize = 1.0f;
float   control_yBoxSize = 1.0f;
float   control_zBoxSize = 1.0f;
float   control_height = 0.0f;
Slider2D control_centre2D;
RadioButton control_projectionMode;

File pdfPath;
boolean waitingToSave = false;
boolean saving = false;

public void setup() {
  
    //this just runs the program in interactive mode
  strokeWeight(0.5f);
  
  loop();
 
  createGUI();
}


public void draw() {
  clear();

  // Read GUI
  control_boxSize = new PVector(control_xBoxSize, control_yBoxSize, control_zBoxSize);
  float vx = -control_centre2D.getArrayValue()[0] * 0.5f * control_xBoxSize;
  float vy =  control_height * 0.5f * control_yBoxSize;
  float vz = control_centre2D.getArrayValue()[1] * control_zBoxSize;
  float snap = 0.01f;
  vx = round(vx/snap)*snap;
  vy = round(vy/snap)*snap;
  vz = round(vz/snap)*snap;
  control_viewCentre = new PVector(vx,vy, vz); 
  
  
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
   
   x = rotateAroundY(x, radians(control_yRotation));
   y = rotateAroundY(y, radians(control_yRotation)); // superfluous really?
   z = rotateAroundY(z, radians(control_yRotation));
   transformedViewCentre = rotateAroundY(transformedViewCentre, radians(control_yRotation));
   
   //println("x rotation = " + control_xRotation);
   x = rotateAroundX(x, radians(control_xRotation));
   y = rotateAroundX(y, radians(control_xRotation));
   z = rotateAroundX(z, radians(control_xRotation));
   transformedViewCentre = rotateAroundX(transformedViewCentre, radians(control_xRotation));
     
   
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
  // float scale = width/3.5;//float(mouseY)/height;
  
  //old fisheye version
  //alpha = atan2(x, sqrt(y*y + z*z)) * scale;
  //beta = atan2(y, sqrt(x*x + z*z))  * scale;
  
  
  // hemispheric polar
  if (control_projectionMode.getValue() == 0)
  {
    alpha = 0.5f*width*x/(sqrt(x*x + y*y + z*z));
    beta = 0.5f*width*y/(sqrt(x*x + y*y + z*z));
  }
  else if (control_projectionMode.getValue() == 1)
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
  String answer = "Studio RGL Curvilinear Grid Generator v1.0";
  answer += "\nwww.twitter.com/RealGoodLiars";
  answer += "\nwww.instagram.com/RealGoodLiars";
  answer += "\nBuilt with Processing and ControlP5 GUI";
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


public void createGUI(){
  println("created GUI");
  cp5 = new ControlP5(this);
  
  int nStackedSliders = 6;
  int sliderSpacing = 48;
  int sliderWidth = 512;
  int buttonWidth = sliderWidth/4;
  int verticalSliderWidth = PApplet.parseInt(sliderSpacing*(nStackedSliders-0.5f));
  int sliderHeight = 32;
  int startHeight = height-sliderSpacing*nStackedSliders-sliderSpacing;
  int xPos = sliderSpacing;
  int yPos = startHeight;

  
  int labelColor = color(0);
  
  Slider s;
  
  s = cp5.addSlider("control_yRotation");
  setupSlider(s, "Y Rotation", -90, 90, xPos, yPos, sliderWidth, sliderHeight, 0.5f);
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_xBoxSize");
  setupSlider(s, "Box Scale X", 0.1f, 20, xPos, yPos, sliderWidth, sliderHeight, 0.1f);
  s.setColorForeground(color(127,0,0));
  s.setColorActive(color(255,0,0));
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_yBoxSize");
  setupSlider(s, "Box Scale Y", 0.1f, 20, xPos, yPos, sliderWidth, sliderHeight, 0.1f);
  s.setColorForeground(color(0, 127, 0));
  s.setColorActive(color(0, 255, 0));
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_zBoxSize");
  setupSlider(s, "Box Scale Z", 0.1f, 20, xPos, yPos, sliderWidth, sliderHeight, 0.1f);
  s.setColorForeground(color(0, 0, 127));
  s.setColorActive(color(0, 0, 255));
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_gridFrequency");
  setupSlider(s, "Grid Frequency", 4, 32, xPos, yPos, sliderWidth, sliderHeight, 1);
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_curvePrecision");
  setupSlider(s, "Curve Precision", 4, 256, xPos, yPos, sliderWidth, sliderHeight, 4);
  yPos += sliderSpacing;
  
  // vertical sliders
  yPos = startHeight;
  xPos += sliderWidth*1.2f;
  
  s = cp5.addSlider("control_xRotation");
  setupSlider(s, "X Rotation", -90, 90, xPos, yPos, sliderHeight, verticalSliderWidth, 0.5f);
  xPos += sliderSpacing;
  
  s = cp5.addSlider("control_height");
  setupSlider(s, "Canera Height", -1, 1, xPos, yPos, sliderHeight, verticalSliderWidth, 0.05f);
  xPos += sliderSpacing;

  control_centre2D = cp5.addSlider2D("")
               .setLabel("Camera Position")
               .setPosition(xPos,yPos)
               .setMinX(-1)
               .setMaxX(1)
               .setMinY(-0.5f)
               .setMaxY(1)
               //.setArrayValue(0, 0)
               //.setArrayValue(1, 0)
               .setSize(verticalSliderWidth,verticalSliderWidth)
               .setCursorX(2.0f)
               .setCursorY(1.0f)
               .setValue(0,0);
  yPos += sliderSpacing; 

   
  xPos = width-buttonWidth/2-2*sliderSpacing;
  cp5.addButton("triggerSave")
                .setLabel("Save PDF")
                .setPosition(xPos, height-2*sliderSpacing)
                .setSize(buttonWidth,sliderHeight)
                ;
  
  control_projectionMode = cp5.addRadioButton("myList-d1");
  control_projectionMode.setPosition(xPos, startHeight);
  control_projectionMode.addItem("Spherical",0);
  control_projectionMode.addItem("Cylindrical", 1);
  control_projectionMode.setColorLabels(color(0));
  control_projectionMode.setItemHeight(sliderHeight);
  control_projectionMode.setItemWidth(sliderHeight);
  control_projectionMode.activate(0);
   
  println ("added sliders ok");
}


public void setupSlider(Slider s, String label, float min, float max, int xPos, int yPos, int sliderWidth, int sliderHeight, float step){
  s.setLabel(label);
  s.setPosition(xPos,yPos);
  s.setRange(min,max);
  s.setColorLabel(color(0));
  s.setSize(sliderWidth, sliderHeight);
  
  if (step != 0) {
      s.setNumberOfTickMarks(PApplet.parseInt((max-min)/step+1));
      s.showTickMarks(false);
      s.snapToTickMarks(true);
      s.setColorTickMark(color(0));
  }
}
  public void settings() {  size(1600, 1600);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "curvilinearGridGenerator" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
