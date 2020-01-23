

void createGUI(){
  println("created GUI");
  cp5 = new ControlP5(this);
  
  int sliderSpacing = 48;
  int sliderWidth = 256;
  int sliderHeight = 32;
  int startHeight = height-sliderSpacing*6-sliderSpacing;
  int xPos = sliderSpacing;
  int yPos = startHeight;

  
  color labelColor = color(0);
  
  Slider s;
  
  s = cp5.addSlider("control_curvePrecision");
  setupSlider(s, "Curve Precision", 4, 256, xPos, yPos, sliderWidth, sliderHeight, 4);
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_gridFrequency");
  setupSlider(s, "Grid Frequency", 4, 64, xPos, yPos, sliderWidth, sliderHeight, 1);
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_yRotation");
  setupSlider(s, "Y Rotation", -360, 360, xPos, yPos, sliderWidth, sliderHeight, 1);
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_xBoxSize");
  setupSlider(s, "Box Scale X", 0.1, 20, xPos, yPos, sliderWidth, sliderHeight, 0.1);
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_yBoxSize");
  setupSlider(s, "Box Scale Y", 0.1, 20, xPos, yPos, sliderWidth, sliderHeight, 0.1);
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_zBoxSize");
  setupSlider(s, "Box Scale Z", 0.1, 20, xPos, yPos, sliderWidth, sliderHeight, 0.1);
  yPos += sliderSpacing;
  
  
  // vertical sliders
  yPos = startHeight;
  xPos += sliderWidth*2;
  
  s = cp5.addSlider("control_xRotation");
  setupSlider(s, "X Rotation", -90, 90, xPos, yPos, sliderHeight, sliderWidth, 1);
  xPos += sliderSpacing;
  
  s = cp5.addSlider("control_height");
  setupSlider(s, "Canera Height", -1, 1, xPos, yPos, sliderHeight, sliderWidth, 0.1);
  xPos += sliderSpacing;

  control_centre2D = cp5.addSlider2D("")
               .setLabel("Camera Position")
               .setPosition(xPos,yPos)
               .setMinX(-1)
               .setMaxX(1)
               .setMinY(-1)
               .setMaxY(1)
               .setSize(sliderWidth,sliderWidth);
  yPos += sliderSpacing; 
   
  println ("added slider ok");
}

void setupSlider(Slider s, String label, float min, float max, int xPos, int yPos, int sliderWidth, int sliderHeight, float step){
  s.setLabel(label);
  s.setPosition(xPos,yPos);
  s.setRange(min,max);
  s.setColorLabel(color(0));
  s.setSize(sliderWidth, sliderHeight);
  
  if (step != 0) {
      s.setNumberOfTickMarks(int((max-min)/step)+1);
      s.showTickMarks(true);
      s.snapToTickMarks(true);
  }
}
