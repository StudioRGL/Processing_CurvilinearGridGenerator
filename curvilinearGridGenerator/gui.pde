

void createGUI(){
  println("created GUI");
  cp5 = new ControlP5(this);
  
  int xPos = 16;
  int yPos = 100;
  int sliderSpacing = 48;
  int sliderWidth = 256;
  int sliderHeight = 32;
  
  color labelColor = color(0);
  
  Slider s = cp5.addSlider("control_curvePrecision");
  setupSlider(s, "Curve Precision", 4, 256, xPos, yPos, sliderWidth, sliderHeight);
  yPos += sliderSpacing;
  
  
  cp5.addSlider("control_gridFrequency")
              .setPosition(xPos,yPos)
              .setRange(10,30);
  yPos += sliderSpacing;
  
  cp5.addSlider("control_xRotation")
              .setPosition(xPos,yPos)
              .setRange(0,255);
  yPos += sliderSpacing; 
  
  cp5.addSlider("control_yRotation")
              .setPosition(xPos,yPos)
              .setRange(0,255);
  yPos += sliderSpacing; 
  
  cp5.addSlider2D("control_centre2D")
               .setPosition(xPos,yPos)
               .setMinX(-1)
               .setMaxX(1)
               .setMinY(-1)
               .setMaxY(1);
  yPos += sliderSpacing; 
  
  println ("added slider ok");
}

void setupSlider(Slider s, String label, float min, float max, int xPos, int yPos, int sliderWidth, int sliderHeight){
  s.setLabel(label);
  s.setPosition(xPos,yPos);
  s.setRange(min,max);
  s.setColorLabel(color(0));
  s.setSize(sliderWidth, sliderHeight);
}
