import g4p_controls.*; //<>//

/*
Copyright 2019 Studio RGL LLP

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

// We don't require you to credit us, but if you use it in your work feel free to point people to our twitter
// https://twitter.com/RealGoodLiars
// https://www.instagram.com/realgoodliars/

// usage:
// 1) run the program (tested using Processing 3.5, you can compile and run standalone if u want)
// 2) pick a file location
// 3) move the mouse to get the grid you want
// 4) click to save a pdf
// 5) done I guess, make some cool animation!

//import g4p_controls.*;
import controlP5.*;  // switched to a different GUI library
import processing.pdf.*;


// control values
int control_curvePrecision = 64;
int control_projectionMode = 0;

ControlP5 cp5;
ControlWindow controlWindow;

float   control_yRotation = 0;
float   control_xRotation = 0;
PVector control_boxSize = new PVector(1,1,1);
PVector control_viewCentre = new PVector(1,1,1);
float   control_gridFrequency = 1;
PVector control_centre2D = new PVector(0,0,0);



File pdfPath;
boolean waitingToSave = false;
boolean saving = false;

void setup() {
  
  size(1600, 1600);  //this just runs the program in interactive mode
  strokeWeight(0.5);
  loop();
 
  createGUI();
}


void draw() {
  clear();

  // Read GUI
  // control_yRotation = radians(slider_yRotation.getValueF());//slider_control_yRotation;//radians(180+360*mouseX/width); //radians(frameCount*10-1);//radians(45);//radians(frameCount-1);
  // control_xRotation = radians(slider_xRotation.getValueF());
  // control_boxSize = new PVector (slider_scale_X.getValueF(), slider_scale_Y.getValueF(), slider_scale_Z.getValueF());
  // control_viewCentre = new PVector (-slider_XZ.getValueXF(), -slider_height.getValueF(), slider_XZ.getValueYF());
  // control_viewCentre = new PVector(control_viewCentre.x * control_boxSize.x, control_viewCentre.y * control_boxSize.y, control_viewCentre.z * control_boxSize.z); 
  // control_gridFrequency = slider_gridFrequency.getValueF();
  // control_curvePrecision = slider_curvePrecision.getValueI();
  
  
  
  
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
  
  
  
 
 
 
  background(64);
  
  
  fill(34);
  text(getDataString(), 32,32);
  noFill();
  
  translate(width/2,height/2);

  
  // now we gonna do all the drawin'
  
   PVector x = new PVector(1,0,0);
   PVector y = new PVector(0,1,0);
   PVector z = new PVector(0,0,1);
   PVector transformedViewCentre = new PVector(1,1,1);//control_viewCentre.copy();
   
   x = rotateAroundY(x, control_yRotation);
   y = rotateAroundY(y, control_yRotation); // superfluous really?
   z = rotateAroundY(z, control_yRotation);
   transformedViewCentre = rotateAroundY(transformedViewCentre, control_yRotation);
   
   //println("x rotation = " + control_xRotation);
   x = rotateAroundX(x, control_xRotation);
   y = rotateAroundX(y, control_xRotation);
   z = rotateAroundX(z, control_xRotation);
   transformedViewCentre = rotateAroundX(transformedViewCentre, control_xRotation);
     
   
  //direction,  perpendicular,  centre,  width,  length, int nLines)
 
  // X PLANES
  stroke(255, 0, 0);
  drawPlane(y,z, PVector.add(transformedViewCentre, x.copy().mult(0.5 * control_boxSize.x)), control_boxSize.z,control_boxSize.y, control_gridFrequency);
  drawPlane(y,z, PVector.sub(transformedViewCentre, x.copy().mult(0.5 * control_boxSize.x)), control_boxSize.z,control_boxSize.y, control_gridFrequency);
  
  // Z PLANE
  stroke(0, 127, 255);
  drawPlane(x,y, PVector.add(transformedViewCentre, z.copy().mult(0.5 * control_boxSize.z)), control_boxSize.y,control_boxSize.x, control_gridFrequency);

  // green for y
  stroke (0,255,0);
  drawPlane(x,z, PVector.sub(transformedViewCentre, y.copy().mult(0.5 * control_boxSize.y)), control_boxSize.z,control_boxSize.x, control_gridFrequency);
  stroke (0, 127,0);
  drawPlane(x,z, PVector.add(transformedViewCentre, y.copy().mult(0.5 * control_boxSize.y)), control_boxSize.z,control_boxSize.x, control_gridFrequency); // ground
  
  
  
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


void drawCross(PVector direction, PVector perpendicular, PVector centre, float crossSize)
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

void drawPlane(PVector direction, PVector perpendicular, PVector centre, float planeWidth, float planeLength, float lineFrequency)
{
  direction.normalize();
  perpendicular.normalize();
  
  int MAX_LINES = 200;
  int nLinesX = max(3, min(MAX_LINES, int(lineFrequency*planeWidth)));
  int nLinesY = max(3, min(MAX_LINES, int(lineFrequency*planeLength)));
  
  
  drawLineArray(direction, perpendicular, centre, planeWidth, planeLength, nLinesX);
  drawLineArray(perpendicular, direction, centre, planeLength, planeWidth, nLinesY);
}



void drawLineArray(PVector direction, PVector perpendicular, PVector centre, float arrayWidth, float lineLength, int nLines){
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
  // float scale = width/3.5;//float(mouseY)/height;
  
  //old fisheye version
  //alpha = atan2(x, sqrt(y*y + z*z)) * scale;
  //beta = atan2(y, sqrt(x*x + z*z))  * scale;
  
  
  // hemispheric polar
  if (control_projectionMode == 0)
  {
    alpha = 0.5*width*x/(sqrt(x*x + y*y + z*z));
    beta = 0.5*width*y/(sqrt(x*x + y*y + z*z));
  }
  else if (control_projectionMode == 1)
  {
    // cylindrical
    alpha = atan2(x,z)*width   *0.25;
    beta = 0.5*width*y/(sqrt(x*x + y*y + z*z)) * 0.5;
  }
  
  return new PVector(alpha, beta, 0);
}





// from  https://processing.org/reference/selectOutput_.html
void fileSelected(File selection) {
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
String getDataString()
{
  String answer = "Studio RGL Curvilinear Grid Generator v0.22";
  answer += "\nwww.twitter.com/RealGoodLiars";
  answer += "\nwww.instagram.com/RealGoodLiars";
  answer += "\nBuilt with Processing and G4P GUI";
  answer += "\nBox Scale: " + control_boxSize;
  answer += "\nView Centre: " + control_viewCentre;
  answer += "\nRotation XY: " + control_xRotation + ", " + control_yRotation;
  
  return answer;
}

void triggerSave()
{
  selectOutput("Select a file to write to:", "fileSelected");
}

// save a pdf if we press the mouse
void mousePressed(){
  // triggerSave();
}
