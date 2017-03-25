/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprite;

import geometry.Vector;
import java.util.ArrayList;
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
    private double target_X;
    private double target_Y;
    public int Health;
    public boolean is_death;
    private long time_shot;
    public ArrayList<Bullet> bullet_arr = new ArrayList<Bullet>();
    private double radius_visible = 400.0;
    private double res_x;
    private double res_y;
    public double speed = 3;
    public double tanker;
    public double limit_tanker = 10000;

    //Конструктор
    public Unit(String type) {
        this.setImage(new Image("img/robo.png"));
        this.target_X = 20;
        this.target_Y = 20;
        this.type = type;
        this.Health = 10000;
        this.setCenterX(20);
        this.setCenterY(20);
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
        } else if ((deltaX < -0.1 & deltaY < -0.1)) {
            this.setCenterX(this.getCenterX() - (speed * Math.cos(direction)));
            this.setCenterY(this.getCenterY() - (speed * Math.sin(direction)));
        } else if ((deltaX > 0.1 & deltaY > 0.1)) {
            this.setCenterX(this.getCenterX() + (speed * Math.cos(direction)));
            this.setCenterY(this.getCenterY() + (speed * Math.sin(direction)));
        } else if ((deltaX > 0.1 & deltaY < -0.1)) {
            this.setCenterX(this.getCenterX() + (speed * Math.cos(direction)));
            this.setCenterY(this.getCenterY() + (speed * Math.sin(direction)));
        } else if ((deltaX < -0.1 & deltaY > 0.1)) {
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

    //Смерть
    public void death() {
        is_death = true;
    }

    public void harvest(Building b) {
        for (Building bb : MyGame.friend_build) {

            if (bb.equals(b)) {
                bb.Health = bb.Health - 1000;
            }
        }
        this.tanker = this.tanker + 1000;

        if (this.tanker > this.limit_tanker) {
            this.target_X = 1000;
            this.target_Y = 1000;

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
        }
    }
}
