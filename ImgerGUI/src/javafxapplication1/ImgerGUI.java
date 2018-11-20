/*
 * ImgerGUI Provides a Graphical User Interface to use the methods in ImgerAPI.
    Copyright (C) 2018  Bernardo Laing

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package javafxapplication1;

import imgerapi.ImgerAPI;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author berna
 */
public class ImgerGUI extends Application {
    
    private final Desktop desktop = Desktop.getDesktop();
    private String filename = "";
    private String extension = "";
    File srcImageFile;
    File outImageFile = new File("out.png");
    BufferedImage srcImg = null;
    BufferedImage outImg = null;
    
    @Override
    public void start(Stage primaryStage) {
        
        ImageView srcImgView = new ImageView();
        srcImgView.setFitWidth(450);
        srcImgView.setPreserveRatio(true);
        ImageView outImgView = new ImageView();
        outImgView.setFitWidth(450);
        outImgView.setPreserveRatio(true);
        
        TextField scaleField = new TextField("1");
        
        ColorPicker filterPicker = new ColorPicker();
        filterPicker.setDisable(true);
        
        FileChooser fc = new FileChooser();
        configureFileChoose(fc);
        
        Button greyscaleBtn = new Button();
        greyscaleBtn.setText("Greyscale");
        greyscaleBtn.setDisable(true);
        greyscaleBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if(srcImg != null){
                    outImg = ImgerAPI.GreyScale(srcImg);
                    outImgView.setImage(SwingFXUtils.toFXImage(outImg, null));
                }
            }
            
        });
        
        Button blurBtn = new Button();
        blurBtn.setText("Blur");
        blurBtn.setDisable(true);
        blurBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if(srcImg != null){
                    outImg = ImgerAPI.Blur(srcImg, 20);
                    outImgView.setImage(SwingFXUtils.toFXImage(outImg, null));
                }
            }
            
        });
        
        Button downscaleBtn = new Button();
        downscaleBtn.setText("Downscale 2x");
        downscaleBtn.setDisable(true);
        downscaleBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if(srcImg != null){
                    outImg = ImgerAPI.Downscale(srcImg);
                    outImgView.setImage(SwingFXUtils.toFXImage(outImg, null));
                }
            }
            
        });
        
        Button resizeBtn = new Button();
        resizeBtn.setText("Resize by factor");
        resizeBtn.setDisable(true);
        resizeBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if(srcImg != null){
                    outImg = ImgerAPI.Resize(srcImg, Float.parseFloat(scaleField.getText()));
                    outImgView.setImage(SwingFXUtils.toFXImage(outImg, null));
                }
            }
        });
        
        Button filterBtn = new Button();
        filterBtn.setText("Apply Filter");
        filterBtn.setDisable(true);
        filterBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if(srcImg != null){
                    int alpha = (int) (filterPicker.getValue().getOpacity() * 255);
                    int red =(int) (filterPicker.getValue().getRed() * 255);
                    int green =(int) (filterPicker.getValue().getGreen() * 255);
                    int blue =(int) (filterPicker.getValue().getBlue() * 255);
                    int color = alpha;
                    color = (color << 8) + red;
                    color = (color << 8) + green;
                    color = (color << 8) + blue;
                    outImg = ImgerAPI.ColorFilter(srcImg, color);
                    outImgView.setImage(SwingFXUtils.toFXImage(outImg, null));
                }
            }
        });
        
        Button saveBtn = new Button();
        saveBtn.setText("Save");
        saveBtn.setDisable(true);
        saveBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                if(srcImg != null){
                    saveImg();
                }
            }
        });
        
        Button btn = new Button();
        btn.setText("Open Image");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                srcImageFile = fc.showOpenDialog(primaryStage);
                if(srcImageFile != null){
                    openFile(srcImageFile);
                    filename = srcImageFile.getName();
                    extension = getFileExtension(srcImageFile);
                    System.out.println("Ext: " + extension);
                    greyscaleBtn.setDisable(false);
                    blurBtn.setDisable(false);
                    downscaleBtn.setDisable(false);
                    resizeBtn.setDisable(false);
                    filterBtn.setDisable(false);
                    filterPicker.setDisable(false);
                    saveBtn.setDisable(false);
                    srcImgView.setImage(SwingFXUtils.toFXImage(srcImg, null));
                    primaryStage.sizeToScene();
                }
            }
        });
        
        
        
        
        final HBox row = new HBox();
        row.setSpacing(10);
        row.getChildren().addAll(greyscaleBtn, blurBtn, downscaleBtn, resizeBtn, scaleField, filterBtn, filterPicker, saveBtn);
        final GridPane gp = new GridPane();
        GridPane.setConstraints(btn, 0, 0);
        GridPane.setConstraints(row, 1, 0);
        GridPane.setConstraints(srcImgView, 0, 1);
        GridPane.setConstraints(outImgView, 1, 1);
        gp.setHgap(10);
        gp.setVgap(10);
        gp.getChildren().addAll(btn, row, srcImgView, outImgView);
        
        final Pane root = new VBox(20);
        
        root.getChildren().add(gp);
        root.setPadding(new Insets(12, 12, 12, 12));
        
        primaryStage.setTitle("Imger");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        
        launch(args);
    }
    
    private static void configureFileChoose(final FileChooser fc){
        fc.setTitle("Open Image");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );
    }
    
    private void openFile(File file){
        try{
//            desktop.open(file);
            srcImg = ImageIO.read(srcImageFile);
        }catch(IOException ex){
            Logger.getLogger(ImgerGUI.class.getName()).log(
                Level.SEVERE, null, ex
            );
        }
    }
    
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf+1);
    }
    
    private void saveImg(){
        try {
            ImageIO.write(outImg, extension, new File("new-"+filename));
            System.out.println("Image saved!");
        } catch (IOException ex) {
            Logger.getLogger(ImgerGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
