

void createGUI(){
  println("created GUI");
  cp5 = new ControlP5(this);
  
  int xPos = 16;
  int yPos = 100;
  int sliderSpacing = 16;
  
  cp5.addSlider("control_curvePrecision")
              .setPosition(xPos,yPos)
              .setRange(0,255);
  yPos += sliderSpacing;
  
  cp5.addSlider("control_gridFrequency")
              .setPosition(xPos,yPos)
              .setRange(0,255);
  yPos += sliderSpacing;
  
  cp5.addSlider("control_xRotation")
              .setPosition(xPos,yPos)
              .setRange(0,255);
  yPos += sliderSpacing; 
  
  cp5.addSlider("control_yRotation")
              .setPosition(xPos,yPos)
              .setRange(0,255);
  yPos += sliderSpacing; 
  
  println ("added slider ok");
}
