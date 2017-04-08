/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprite;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;

/*
*  Здания и селект
 */
public class Building extends Rectangle {

    private Image image;
    private double positionX;
    private double positionY;
    public double width;
    public double height;
    public String type;
    public int Health;
    public boolean is_death;

    //Конструктор
    public Building(String type) {
        if (type.equals("res")) {
            this.setImage(new Image("img/sun.png"));
            this.Health = 50000;
        } 
        else if(type.equals("base")) {
            this.setImage(new Image("img/base3.png"));
            this.Health = 3000;
        }
        else if(type.equals("refinary")){
            this.setImage(new Image("img/refinary1.png"));
            this.Health = 10000;
        }
         else if(type.equals("factory")){
            this.setImage(new Image("img/base0.png"));
            this.Health = 10000;
        }
        
        
        //this.positionX = 10;
        //this.positionY = 10;
        this.type = type;
        
    }

    //Рисунок
    public void setImage(Image i) {
        this.image = i;
        this.width = i.getWidth();
        this.height = i.getHeight();
        this.setWidth(width); // Присваиваем ширину рисунка - прямоугольнику
        this.setHeight(height); // Присваиваем высоту рисунка - прямоугольнику
    }

    //Установщики
    //Положение на сцене--------------------------------------------------------
    public void setPosition(double x, double y) {
        positionX = x;
        positionY = y;
    }

    public void setPositionX(double positionX) {
        this.positionX = positionX;
        this.setX(positionX);
    }

    public void setPositionY(double positionY) {
        this.positionY = positionY;
        this.setY(positionY);
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    //--------------------------------------------------------------------------
    // Отрисовка и определение 
    public void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY);
    }

    //Спрайт для селекта
    public void render(GraphicsContext gc, double width, double hight) {
        gc.drawImage(image, positionX, positionY, width, hight);
        this.width = width;
        this.height = hight;
    }

    //--------------------------------------------------------------------------    
    //Проверка коллизий 
    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, Math.abs(width), Math.abs(height));
    }

  
    public boolean intersects(Unit s) {
        return s.getBoundary().intersects(this.getBoundary());
    }
    
    //--------------------------------------------------------------------------    
    //Проверка коллизий для селекта
    public Rectangle2D getBoundary(Double x, Double y) {
        Rectangle2D rec = new Rectangle2D(positionX, positionY, Math.abs(width), Math.abs(height));

        //Поправки на неправильное выделение
        if (width < 0 & height < 0) {
            rec = new Rectangle2D(x, y, Math.abs(width), Math.abs(height));
        } else if (width < 0 & height > 0) {
            rec = new Rectangle2D(x, positionY, Math.abs(width), Math.abs(height));
            System.out.println("bingo1");
        } else if (width > 0 & height < 0) {
            rec = new Rectangle2D(positionX, y, Math.abs(width), Math.abs(height));
            System.out.println("bingo2");
        }
        return rec;
    }
  
    public boolean intersects(Unit s, Double x, Double y) {
        return s.getBoundary().intersects(this.getBoundary(x,y));
    }
    
    //Уничтожение
    public void death() {
        is_death = true;
    }

    public void setType(String type) {
        this.type = type;
    }
}
