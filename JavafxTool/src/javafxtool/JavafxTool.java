/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxtool;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import javafx.scene.paint.Color;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author bsbos
 */
public class JavafxTool extends Application {

    public static LinkedList<pixel> boundries = new LinkedList<pixel>();
    public static PixelReader pxReader;
    public static int[] dx = {0 , 0 , 1 , -1};
    public static int[] dy = {1 , -1 , 0 , 0};
    public static boolean[][] vis = new boolean[905][545];
    public static Color initColor;
    public static Image inputImg;
    public static PixelWriter pxWriter;
    public static WritableImage img;
    
    public static void memset(){
        for(int i=0;i<901;++i)
            for(int j=0;j<541;++j)
                vis[i][j] = false;
    }
    
    public static boolean valid(int x , int y){
        return x < 900 && x > 0 && y > 0 && y < 540;
    }

    public static boolean validDef(pixel pix , pixel newpix) {
        Color pixColor = pxReader.getColor(pix.x , pix.y);
        Color newpixColor = pxReader.getColor(newpix.x,newpix.y);
        double def = Math.abs( pixColor.getRed() - newpixColor.getRed()  ) + Math.abs( pixColor.getBlue() - newpixColor.getBlue()  ) + Math.abs( pixColor.getGreen() - newpixColor.getGreen()  );
        return def < 0.020;
    }

    public static void dfs(int x , int y) {
        vis[x][y] = true;
        
        for(int i=0;i<4;++i) {
            int newx = x + dx[i];
            int newy = y + dy[i];
            pixel pix = new pixel(x , y);
            pixel newpix = new pixel(newx , newy);
            boolean validDefVar = validDef(pix , newpix);
            if( valid(newx , newy) && !vis[newx][newy] && validDefVar) {
                dfs(newx , newy);
            } else if(boundries.contains(newpix)) {
                boundries.remove(newpix);            /* state of facing boundries*/
            } else if(!validDefVar) {
                boundries.add(newpix);            /*state of facing a wall*/
            }
        }
        
        return ;
    }

    public static void bfs(int x, int y) {
        vis[x][y] = true;
        Queue<pixel> q = new LinkedList<pixel>();
        q.add(new pixel(x,y));
        
        while(!q.isEmpty()){
            pixel cur = q.peek();
            q.poll();
            for(int i=0;i<4;++i) {
                int newx = cur.x + dx[i];
                int newy = cur.y + dy[i];
                pixel newpix = new pixel(newx , newy);
                
                if( !valid(newx , newy) )
                    continue;
                
                boolean validDefVar = validDef(cur , newpix);
                if(!vis[newx][newy] && validDefVar) {
                    Color tmp = pxReader.getColor(newx , newy);  tmp = tmp.darker();
                    pxWriter.setColor( newx , newy , tmp.darker() );
                    q.add(newpix);
                    vis[newx][newy] = true;
                } else if(boundries.contains(newpix)) {
                    boundries.remove(newpix);            /* state of facing boundries*/
                } else if(!validDefVar) {
                    boundries.add(newpix);            /*state of facing a wall*/
                }
            }
        }
    }
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        
        inputImg = new Image("File:image/photo.jpg");
        img = new WritableImage((int)inputImg.getWidth() , (int)inputImg.getHeight());
        pxReader = inputImg.getPixelReader();
        pxWriter  = img.getPixelWriter();
        
        for(int i=0;i<inputImg.getWidth();++i)
            for(int j=0;j<inputImg.getHeight();++j)
            {
                Color tmp = pxReader.getColor(i, j);
                pxWriter.setColor(i , j , tmp );
            }
        Pane root = new Pane();
        ImageView im = new ImageView(img);
        root.getChildren().add( im );
        
        TextField tf = new TextField();
        tf.setLayoutX(750);
        tf.setLayoutY(500);
        root.getChildren().add(tf);
        Scene scene = new Scene(root, 900, 540);
        scene.setOnMouseClicked(e->{
            int x = (int)e.getSceneX();
            int y = (int)e.getSceneY();
//            initColor = px.getColor(x, y);
            memset();
            bfs(x , y);
            String temp = "";
            for(pixel itr:boundries)
                temp += " (" + itr.x + "," + itr.y + ") "; /* format of printing boundries */
            tf.setText(temp);
        });
        
        primaryStage.setTitle("");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public static class pixel {
        public int x , y;
        public pixel(int x, int y){ this.x = x; this.y = y; }
    }
}
