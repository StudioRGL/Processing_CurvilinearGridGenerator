/* //<>// //<>//
Copyright 2019 Studio RGL LLP

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

import controlP5.*;  // for UI: http://www.sojamo.de/libraries/controlP5
import processing.pdf.*;  // for PDF export

// github version
String versionString = "v0.1.0-alpha";

// control values 
int     control_curvePrecision = 32;
float   control_yRotation = 0;
float   control_xRotation = 0;
PVector control_boxSize = new PVector(1,1,1);
PVector control_viewCentre = new PVector(0,0,0);
float   control_gridFrequency = 16;
float   control_xBoxSize = 1.0;
float   control_yBoxSize = 1.0;
float   control_zBoxSize = 1.0;
float   control_height = 0.0;
float   control_extend = 0.0;

// other settings
float viewSnap = 0.01;
float pixelDrawSize;  // how big to draw the thing. probably the screen height, but we don't know until after setup()
File exportPath;
boolean waitingToSave = false;
boolean saving = false;

// UI stuff
ControlP5 cp5;
ControlWindow controlWindow;
Slider2D control_centre2D;
RadioButton control_projectionMode;
RadioButton control_saveMode;


void setup() {
  fullScreen();
  pixelDrawSize = min(displayWidth, displayHeight) * 0.9; // the size multiplier for the final shape
  strokeWeight(0.5);  // set it to 0.5 for regular use, 2 for screen capture for instagram etc
  smooth();
  loop();
  createGUI();
}


void draw() {
  clear();

  // Read GUI values
  control_boxSize = new PVector(control_xBoxSize, control_yBoxSize, control_zBoxSize);
  float vx = -control_centre2D.getArrayValue()[0];
  float vy =  control_height;
  float vz = control_centre2D.getArrayValue()[1];

  // snap the values so they're easier to recreate
  vx = snap(vx, viewSnap);
  vy = snap(vy, viewSnap);
  vz = snap(vz, viewSnap);
  
  // scale them to the full size of the box
  vx = vx * 0.5 * control_xBoxSize;
  vy = vy * 0.5 * control_yBoxSize;
  vz = vz * 0.5 * control_zBoxSize;
  
  control_viewCentre = new PVector(vx,vy, vz); 
  
  
  // setup saving, if required
  String exportLocation = "";
  if (exportPath != null)
  {
    exportLocation = exportPath.getAbsolutePath();
    
    // regular saver
    if (control_saveMode.getValue() == 0){
      exportLocation += ".pdf";
    }
    else if (control_saveMode.getValue() == 1){
      exportLocation += ".svg";
    }
    else if (control_saveMode.getValue() == 2){
      exportLocation += ".png";
    }
    
    // hacky autosaver, used for generating consistently named sample files
    /*
    String data = "";
    int rounding = 2;
    if (control_projectionMode.getValue() == 0){
      data += "spherical";
    }
    else{
      data += "cylindrical";
    }
    data += "_scale_" + nf(control_boxSize.x, 0, rounding) + "_"  + nf(control_boxSize.y, 0, rounding) + "_"  + nf(control_boxSize.z, 0, rounding);
    data += "_centre_" + nf(control_centre2D.getArrayValue()[0], 0, rounding) + "_" +  nf(control_height, 0, rounding) + "_" +  nf(control_centre2D.getArrayValue()[1], 0, rounding);
    data += "_rotation_" + nf(control_xRotation, 0, 1) + "_" + nf(control_yRotation, 0, 1);
    exportLocation = ("C:/Temp/grid_" + data + ".pdf"); // auto save override here
    */
    
    if (waitingToSave)
    {
      println ("Saving: " + exportLocation);
      if (control_saveMode.getValue() == 0){
        beginRecord(PDF, exportLocation);
      }
      else if (control_saveMode.getValue() == 1){
        beginRecord(SVG, exportLocation);
      }
    
      // we don't need to begin record for png
      
      waitingToSave = false;
      saving = true;
    }
  }
  
  
  // do the drawing
  background(255);
  fill(34);
  text(getDataString(), 48, 48);
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
 
  float reversedXRotation = -control_xRotation; // flip this because the slider 'feels' upside down
  x = rotateAroundX(x, radians(reversedXRotation));
  y = rotateAroundX(y, radians(reversedXRotation));
  z = rotateAroundX(z, radians(reversedXRotation));
  transformedViewCentre = rotateAroundX(transformedViewCentre, radians(reversedXRotation));
   
  // X PLANES
  stroke(255, 0, 0);
  drawPlane(y,z, PVector.add(transformedViewCentre, x.copy().mult(0.5 * control_boxSize.x)), control_boxSize.z,control_boxSize.y, control_gridFrequency, control_extend, control_extend);
  drawPlane(y,z, PVector.sub(transformedViewCentre, x.copy().mult(0.5 * control_boxSize.x)), control_boxSize.z,control_boxSize.y, control_gridFrequency, control_extend, control_extend);
  
  // Z PLANE
  stroke(0, 127, 255);
  drawPlane(x,y, PVector.add(transformedViewCentre, z.copy().mult(0.5 * control_boxSize.z)), control_boxSize.y,control_boxSize.x, control_gridFrequency, control_extend, control_extend);
  drawPlane(x,y, PVector.sub(transformedViewCentre, z.copy().mult(0.5 * control_boxSize.z)), control_boxSize.y,control_boxSize.x, control_gridFrequency, control_extend, control_extend);

  // green for y
  stroke (0,255,0);
  drawPlane(x,z, PVector.sub(transformedViewCentre, y.copy().mult(0.5 * control_boxSize.y)), control_boxSize.z,control_boxSize.x, control_gridFrequency, control_extend, control_extend);
  stroke (0, 127,0);
  drawPlane(x,z, PVector.add(transformedViewCentre, y.copy().mult(0.5 * control_boxSize.y)), control_boxSize.z,control_boxSize.x, control_gridFrequency, control_extend, control_extend); // ground
  
  // draw vanishing points
  stroke(127);
  float vpDistance = 512;
  float crossSize = vpDistance; // /32;
  drawCross(y, x, PVector.add(transformedViewCentre, z.copy().mult(vpDistance)), crossSize);
  drawCross(y, x, PVector.sub(transformedViewCentre, z.copy().mult(vpDistance)), crossSize);
  drawCross(y, z, PVector.add(transformedViewCentre, x.copy().mult(vpDistance)), crossSize);
  drawCross(y, z, PVector.sub(transformedViewCentre, x.copy().mult(vpDistance)), crossSize);
  drawCross(x, z, PVector.sub(transformedViewCentre, y.copy().mult(vpDistance)), crossSize);
  drawCross(x, z, PVector.sub(transformedViewCentre, y.copy().mult(vpDistance)), crossSize);
  
  
  // stop recording (if we were recording)
  if (saving == true)
  {
    if (control_saveMode.getValue()==2){
        println("Saving PNG");
        save(exportLocation);
      }
    else{
      endRecord();
    }
    saving = false;
    println("Save completed!");
  }

  translate(-width/2, -height/2); // put it back, or we're gonna lose the GUI!
}


void drawCross(PVector direction, PVector perpendicular, PVector centre, float crossSize)
{
  PVector left = centre.copy();
  PVector bottom = centre.copy();
  PVector centreToLeft = perpendicular.copy().mult(-crossSize/2);
  PVector centreToBottom = direction.copy().mult(-crossSize/2);
  left.add(centreToLeft);
  bottom.add(centreToBottom);
   
  // draw cross
  drawLine(perpendicular, left, crossSize);
  drawLine(direction, bottom, crossSize);
}


void drawPlane(PVector direction, PVector perpendicular, PVector centre, float planeWidth, float planeLength, float lineFrequency, float extendX, float extendY)
{
  direction.normalize();
  perpendicular.normalize();
  
  int MAX_LINES = 200;
  int nLinesX = max(3, min(MAX_LINES, int(lineFrequency*planeWidth)));
  int nLinesY = max(3, min(MAX_LINES, int(lineFrequency*planeLength)));
  
  drawLineArray(direction, perpendicular, centre, planeWidth, planeLength+extendX, nLinesX);
  drawLineArray(perpendicular, direction, centre, planeLength, planeWidth+extendY, nLinesY);
}


void drawLineArray(PVector direction, PVector perpendicular, PVector centre, float arrayWidth, float lineLength, int nLines){
  PVector centreToEdge = new PVector();
  PVector centreToBottom = new PVector();
  PVector bottomCentre = new PVector();
  PVector bottomCorner = new PVector();
  PVector lineStep  = new PVector();
  
  PVector.mult(perpendicular, -0.5*arrayWidth, centreToEdge); // calculate vector from centre to edge
  PVector.mult(direction, -0.5*lineLength, centreToBottom); // calculate vector from centre to bottom
  PVector.add(centre, centreToBottom, bottomCentre); // calculate where the bottom centre is
  PVector.add(bottomCentre, centreToEdge, bottomCorner); // calculate where the bottom corner is
  PVector.mult(perpendicular, arrayWidth, lineStep);
  
  for (int iLine = 0; iLine < nLines; iLine++)
  {
    PVector lineStart = lineStep.copy();
    PVector lineDirection = direction.copy();
 
    lineStart.mult(float(iLine)/(nLines-1)); // that's the step, if there's 5 lines there's 4 spaces
    lineStart.add(bottomCorner);
    
    drawLine(lineDirection, lineStart, lineLength);
        
  }
}


// subroutine that draws a line curvilinearly (sp?)
void drawLine(PVector step, PVector start, float desiredLineLength){
  int nPoints = ceil((1+pow(desiredLineLength, 0.5))*control_curvePrecision); // changed that so desired line length extends precision
  
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
  
  // calculate the polar positions of the xyz points 
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
          break;
        }
      }
    }
    if (drawing)
    {
      endShape();
    }
}


// just rotates a vector
PVector rotateAroundY( PVector source, float angle){
  PVector xz = new PVector (source.x, source.z);
  xz = xz.rotate(angle);
  return new PVector(xz.x, source.y, xz.y);
}


// just rotates a vector
PVector rotateAroundX( PVector source, float angle){
  PVector yz = new PVector (source.y, source.z);
  yz = yz.rotate(angle);
  return new PVector(source.x, yz.x, yz.y);
}


//gets coordinates of a xyz point in 2D space
PVector getCoordinates(float x, float y, float z){
  float alpha = 0;
  float beta = 0;
  
  //old fisheye version
  //alpha = atan2(x, sqrt(y*y + z*z)) * scale;
  //beta = atan2(y, sqrt(x*x + z*z))  * scale;
  
  // hemispheric polar
  if (control_projectionMode.getValue() == 0)
  {
    alpha = 0.5*pixelDrawSize*x/(sqrt(x*x + y*y + z*z));
    beta = 0.5*pixelDrawSize*y/(sqrt(x*x + y*y + z*z));
  }
  else if (control_projectionMode.getValue() == 1)
  {
    // cylindrical
    alpha = atan2(x,z)*pixelDrawSize   *0.5;
    beta = 0.5*pixelDrawSize*y/(sqrt(x*x + y*y + z*z)); // assumes your screen is at least 2:1! no portrait support
  }
  
  return new PVector(alpha, beta, 0);
}


// from  https://processing.org/reference/selectOutput_.html
void fileSelected(File selection) {
  if (selection == null)
  {
    exportPath = null;
    waitingToSave = false;
  }
  else
  {
    exportPath = selection;
    waitingToSave = true;
    println("Waiting to save: " + selection.getAbsolutePath());
  }
}


String getDataString()
{
  // just gets all the data for printing
  int rounding = 2;
  String answer = "Studio RGL Curvilinear Grid Generator " + versionString;
  answer += "\nwww.twitter.com/RealGoodLiars";
  answer += "\nwww.instagram.com/RealGoodLiars";
  answer += "\nBuilt with Processing and ControlP5 GUI";
  answer += "\nBox Scale: " + nf(control_boxSize.x, 0, rounding) + ", "  + nf(control_boxSize.y, 0, rounding) + ", "  + nf(control_boxSize.z, 0, rounding);
  answer += "\nView Centre: " + nf(control_centre2D.getArrayValue()[0], 0, rounding) + ", " +  nf(control_height, 0, rounding) + ", " +  nf(control_centre2D.getArrayValue()[1], 0, rounding);
  answer += "\nRotation XY: " + nf(control_xRotation, 0, rounding) + ", " + nf(control_yRotation, 0, rounding);
  
  return answer;
}


void triggerSave()
{
  selectOutput("Select a file to write to:", "fileSelected");
}


float snap(float n, float amount)
{
  return (round(n/amount)*amount);
}
