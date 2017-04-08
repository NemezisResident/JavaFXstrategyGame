/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprite;

import geometry.Vector;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import mygame.MyGame;

/*
* Класс Юнитов
*
 */
public class Unit extends Circle {

    public Image image;
    public double width;
    public double height;
    public String type;
    public double target_X;
    public double target_Y;
    public int Health;
    public boolean is_death;    
    public ArrayList<Bullet> bullet_arr = new ArrayList<Bullet>();
    private double radius_visible = 400.0;   
    public double speed = 3;   
    public double tanker; // значение ресов в юните
    public double limit_tanker = 5000;  
    private double res_x;
    private double res_y;
    private double base_jps_x;
    private double base_jps_y;
    
    //Счетчики времени
    private long time_res;
    private long time_shot;   
        

    //Конструктор
    public Unit(String type, double x, double y) {
        this.setImage(new Image("img/robo.png"));
        this.target_X = x;
        this.target_Y = y;
        this.type = type;
        this.Health = 10000;
        this.setCenterX(x);
        this.setCenterY(y);
        this.setRadius(5);
    }
    
    //Конструктор
    public Unit(String type) {
        this.setImage(new Image("img/robo.png"));       
        this.type = type;
        this.Health = 10000;       
        this.setRadius(5);
    }

    //Рисунок
    public void setImage(Image i) {
        this.image = i;
        this.width = i.getWidth();
        this.height = i.getHeight();
    }

    //Установщики
    //Положение на сцене--------------------------------------------------------  
    //--------------------------------------------------------------------------
    // Отрисовка и определение (Вписание в центр окружности центр прямоугольника)
    public void render(GraphicsContext gc) {
        gc.drawImage(this.image, this.getCenterX() - (this.width / 2), this.getCenterY() - (this.height / 2));
    }

    // Возврат границ прямоугольника
    public Rectangle2D getBoundary() {
        return new Rectangle2D(this.getCenterX() - (this.width / 2), this.getCenterY() - (this.height / 2),
                this.width + 5, this.height + 5);
    }

    //Получить врагов в радиусе видимости
    public Circle getAround(double radius_visible) {
        return new Circle(this.getCenterX(), this.getCenterY(), radius_visible);
    }
    //--------------------------------------------------------------------------

    //Проверка коллизий
    public boolean intersects(Unit s) {
        return s.getBoundary().intersects(this.getBoundary());
    }

    public boolean intersects_enemy(Unit s) {
        return s.getAround(radius_visible).contains(this.getCenterX(), this.getCenterY());
    }

    //Установка координат цели движения
    public void setTarget_X(double target_X) {
        this.target_X = target_X;
    }

    public void setTarget_Y(double target_Y) {
        this.target_Y = target_Y;
    }

    //Метод для движения к цели спрайтов
    public void runner() {        
        double deltaX;
        double deltaY;
        double direction;

        deltaX = target_X - this.getCenterX();
        deltaY = target_Y - this.getCenterY();
        direction = Math.atan(deltaY / deltaX);

        if (this.contains(target_X, target_Y)) {
            //System.out.println("BINGO " + count); 
        } else if ((deltaX < -0.01 & deltaY < -0.01)) {
            this.setCenterX(this.getCenterX() - (speed * Math.cos(direction)));
            this.setCenterY(this.getCenterY() - (speed * Math.sin(direction)));
        } else if ((deltaX > 0.01 & deltaY > 0.01)) {
            this.setCenterX(this.getCenterX() + (speed * Math.cos(direction)));
            this.setCenterY(this.getCenterY() + (speed * Math.sin(direction)));
        } else if ((deltaX > 0.01 & deltaY < -0.01)) {
            this.setCenterX(this.getCenterX() + (speed * Math.cos(direction)));
            this.setCenterY(this.getCenterY() + (speed * Math.sin(direction)));
        } else if ((deltaX < -0.01 & deltaY > 0.01)) {
            this.setCenterX(this.getCenterX() - (speed * Math.cos(direction)));
            this.setCenterY(this.getCenterY() - (speed * Math.sin(direction)));
        }
    }

    //Стрельба
    public void shot(Unit spM, Unit spE) {
        Bullet b = new Bullet(spM, spE);
        bullet_arr.add(b);
    }

    //Время стрельбы
    public long getTime_shot() {
        return time_shot;
    }

    public void setTime_shot(long time_shot) {
        this.time_shot = time_shot;
    }

    public long getTime_res() {
        return time_res;
    }

    public void setTime_res(long time_res) {
        this.time_res = time_res;
    }

    public double getBase_jps_x() {
        return base_jps_x;
    }

    public void setBase_jps_x(double base_jps_x) {
        this.base_jps_x = base_jps_x;
    }

    public double getBase_jps_y() {
        return base_jps_y;
    }

    public void setBase_jps_y(double base_jps_y) {
        this.base_jps_y = base_jps_y;
    }
    
    
        
    //Смерть
    public void death() {
        is_death = true;
    }

    //Сбор урожая
    public void harvest(Building b) {

        //Если заполнился то на базу
        if (this.tanker >= this.limit_tanker) {
            this.target_X = this.base_jps_x;
            this.target_Y = this.base_jps_y;
            return;
        }
        
        for (int i = 0; i < MyGame.friend_build.size(); i++) {
            if (MyGame.friend_build.get(i).equals(b)) {
                MyGame.friend_build.get(i).Health = MyGame.friend_build.get(i).Health - 100;
                
                //System.out.println(MyGame.friend_build.get(i).Health);                
            }
        }
        this.tanker = this.tanker + 100;       
    }
    
    //Выгруза урожая 
    public void unload_res() {          
        MyGame.setResourse(MyGame.getResourse() + this.tanker);  
        this.tanker = 0;
        this.get_res_target();
    }
    
    //Получение координат ближайших ресурсов
    public void get_res_target(){ 
        
        Vector vec = null;
        double vec_t = 100000.0;
        Building item_v = null;
        
        Random randNumber = new Random();
        double random = randNumber.nextDouble();
        
            for (int i = 0; i < MyGame.friend_build.size(); i++) {
            if (MyGame.friend_build.get(i).type.equals("res")) {
                vec = new Vector(this.getCenterX(), this.getCenterY(), MyGame.friend_build.get(i).getPositionX(), MyGame.friend_build.get(i).getPositionY());

                if (vec.get_long() < vec_t) {
                    vec_t = vec.get_long();
                    item_v = MyGame.friend_build.get(i);
                }
            }
        }
        if (item_v!=null){
        this.target_X = item_v.getPositionX() + random + 60;
        this.target_Y = item_v.getPositionY() + random + 60;       
        }
        else {
         this.target_X = this.base_jps_x;
         this.target_Y = this.base_jps_y;        
        }                
    }

    public double getTarget_X() {
        return target_X;
    }

    public double getTarget_Y() {
        return target_Y;
    }    
}

 /*
            //this.image = new Image("img/laser1.png");      
            ImageView iv = new ImageView(this.image);
            Vector vectorA = new Vector(this.getCenterX(), this.getCenterY(), this.getCenterX() + 100.0, this.getCenterY());
            Vector vectorB = new Vector(this.getCenterX(), this.getCenterY(), this.target_X, this.target_Y);

            //Получаем угол поворота
            Double radAngle = Vector.getAngle(vectorA, vectorB);

            //Залепа
            if (vectorB.getB() < 0) {
                iv.setRotate(-radAngle);
            } else {
                iv.setRotate(radAngle);
            }

            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            Image rotatedImage = iv.snapshot(params, null);
            this.setImage(rotatedImage);
         */
