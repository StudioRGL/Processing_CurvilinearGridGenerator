

void createGUI(){
  println("created GUI");
  cp5 = new ControlP5(this);
  
  int nStackedSliders = 7;
  int sliderSpacing = 48;
  int sliderWidth = 256;
  int buttonWidth = sliderWidth/4;
  int verticalSliderWidth = int(sliderSpacing*(nStackedSliders-0.5));
  int sliderHeight = 32;
  int startHeight = height - (sliderSpacing * nStackedSliders) - sliderSpacing - (verticalSliderWidth);
  int xPos = sliderSpacing;
  int yPos = startHeight;

  
  color labelColor = color(0);
  
  Slider s;
  
  s = cp5.addSlider("control_yRotation");
  setupSlider(s, "Y Rotation", -90, 90, xPos, yPos, sliderWidth, sliderHeight, 0.5);
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_xBoxSize");
  setupSlider(s, "Box Scale X", 0.1, 20, xPos, yPos, sliderWidth, sliderHeight, 0.1);
  s.setColorForeground(color(127,0,0));
  s.setColorActive(color(255,0,0));
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_yBoxSize");
  setupSlider(s, "Box Scale Y", 0.1, 20, xPos, yPos, sliderWidth, sliderHeight, 0.1);
  s.setColorForeground(color(0, 127, 0));
  s.setColorActive(color(0, 255, 0));
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_zBoxSize");
  setupSlider(s, "Box Scale Z", 0.1, 20, xPos, yPos, sliderWidth, sliderHeight, 0.1);
  s.setColorForeground(color(0, 0, 127));
  s.setColorActive(color(0, 0, 255));
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_extend");
  setupSlider(s, "Grid Extend", 0, 128, xPos, yPos, sliderWidth, sliderHeight, 0.1);
  yPos += sliderSpacing; 
  
  s = cp5.addSlider("control_gridFrequency");
  setupSlider(s, "Grid Frequency", 4, 32, xPos, yPos, sliderWidth, sliderHeight, 1);
  yPos += sliderSpacing;
  
  s = cp5.addSlider("control_curvePrecision");
  setupSlider(s, "Curve Resolution", 4, 64, xPos, yPos, sliderWidth, sliderHeight, 4);
  yPos += sliderSpacing;
  
  // vertical sliders
  xPos = sliderSpacing;  // reset horizontal
  
  s = cp5.addSlider("control_xRotation");
  setupSlider(s, "X Rotation", -90, 90, xPos, yPos, sliderHeight, verticalSliderWidth, 0.5);
  xPos += sliderSpacing;
  
  s = cp5.addSlider("control_height");
  setupSlider(s, "Height", -1, 1, xPos, yPos, sliderHeight, verticalSliderWidth, 0.05);
  xPos += sliderSpacing;

  control_centre2D = cp5.addSlider2D("")
               .setLabel("Camera Position")
               .setPosition(xPos,yPos)
               .setMinX(-1)
               .setMaxX(1)
               .setMinY(-1)
               .setMaxY(1)
               //.setArrayValue(0, 0)
               //.setArrayValue(1, 0)
               .setSize(verticalSliderWidth,verticalSliderWidth)
               .setCursorX(2.0)
               .setCursorY(1.0)
               .setValue(0,0);
  yPos += sliderSpacing; 

  
  // add save button
  xPos = width-buttonWidth/2-2*sliderSpacing;
  cp5.addButton("triggerSave")
                .setLabel("Save PDF")
                .setPosition(xPos, height-2*sliderSpacing)
                .setSize(buttonWidth,sliderHeight)
                ;
  
  xPos = sliderSpacing;
  yPos = startHeight-sliderSpacing-sliderHeight;
  // add control for projection mode
  control_projectionMode = cp5.addRadioButton("myList-d1");
  control_projectionMode.setPosition(xPos, yPos);
  control_projectionMode.addItem("Spherical",0);
  control_projectionMode.addItem("Cylindrical", 1);
  control_projectionMode.setColorLabels(color(0));
  control_projectionMode.setItemHeight(sliderHeight);
  control_projectionMode.setItemWidth(sliderHeight);
  control_projectionMode.activate(0);
  
  println ("added sliders ok");
}

void setupSlider(Slider s, String label, float min, float max, int xPos, int yPos, int sliderWidth, int sliderHeight, float step){
  s.setLabel(label);
  s.setPosition(xPos,yPos);
  s.setRange(min,max);
  s.setColorLabel(color(0));
  s.setSize(sliderWidth, sliderHeight);
  
  if (step != 0) {
      s.setNumberOfTickMarks(int((max-min)/step+1));
      s.showTickMarks(false);
      s.snapToTickMarks(true);
      s.setColorTickMark(color(0));
  }
}
