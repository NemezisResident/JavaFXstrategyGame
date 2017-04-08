/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprite;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import mygame.MyGame;
import geometry.Vector;
import javafx.geometry.Rectangle2D;

/*
*Класс выстрелов (лазер)
*
 */
public class Bullet extends ImageView {

    private Image image;
    private Unit enemy;
    private Building enemyB;
    private double positionX;
    private double positionY;
    private double target_X;
    private double target_Y;
    public boolean is_death;
    private double width;
    private double height;
    double radAngle;

    //Конструктор для юнита
    public Bullet(Unit me, Unit enemy) {
        this.image = new Image("img/laser1.png");
        ImageView iv = new ImageView(this.image);
        Vector vectorA = new Vector(me.getCenterX(), me.getCenterY(), me.getCenterX() + 100.0, me.getCenterY());
        Vector vectorB = new Vector(me.getCenterX(), me.getCenterY(), enemy.getCenterX(), enemy.getCenterY());

        //Получаем угол поворота
        radAngle = Vector.getAngle(vectorA, vectorB);

        //Залепа
        if (vectorB.getB() < 0) {
            this.radAngle = 360 - radAngle;
            iv.setRotate(radAngle);
        } else {
            iv.setRotate(radAngle);
        }

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = iv.snapshot(params, null);
        this.setImage(rotatedImage);

        this.width = this.image.getWidth();
        this.height = this.image.getHeight();
        this.setPosition(me.getCenterX(), me.getCenterY());
        this.enemy = enemy;

        this.target_X = enemy.getCenterX();
        this.target_Y = enemy.getCenterY();

        //Костыль поправка на баг с углами
        if (radAngle > 110 & radAngle < 170) {
            this.setPositionX(me.getCenterX() - (me.height / 2));
            this.target_Y = enemy.getCenterY() - (enemy.height / 2);
        } else if (radAngle > 190 & radAngle < 260) {
            this.setPositionX(me.getCenterX() - (me.height / 2));
            this.setPositionY(me.getCenterY() - (me.height / 2));
        } else if (radAngle > 270 & radAngle < 350) {
            this.setPositionY(me.getCenterY() - (me.height / 2));
            this.target_Y = enemy.getCenterY() - (enemy.height / 2);
        }
    }

    //Конструктор для здания
    public Bullet(Unit me, Building enemy) {
        this.image = new Image("img/laser1.png");
        ImageView iv = new ImageView(this.image);
        Vector vectorA = new Vector(me.getCenterX(), me.getCenterY(), me.getCenterX() + 100.0, me.getCenterY());
        Vector vectorB = new Vector(me.getCenterX(), me.getCenterY(), enemy.getPositionX() + (enemy.width / 2),
                enemy.getPositionY() + (enemy.height / 2));

        //Получаем угол поворота
        radAngle = Vector.getAngle(vectorA, vectorB);

        //Залепа
        if (vectorB.getB() < 0) {
            this.radAngle = 360 - radAngle;
            iv.setRotate(radAngle);
        } else {
            iv.setRotate(radAngle);
        }

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = iv.snapshot(params, null);
        this.setImage(rotatedImage);

        this.width = this.image.getWidth();
        this.height = this.image.getHeight();
        this.setPosition(me.getCenterX(), me.getCenterY());
        this.enemyB = enemy;

        this.target_X = enemy.getPositionX() + (enemy.width / 2);
        this.target_Y = enemy.getPositionY() + (enemy.height / 2);

        //Костыль поправка на баг с углами
        if (radAngle > 110 & radAngle < 170) {
            this.setPositionX(me.getCenterX() - (me.height / 2));
            //this.target_Y = enemy.getCenterY() - (enemy.height / 2);
        } else if (radAngle > 190 & radAngle < 260) {
            this.setPositionX(me.getCenterX() - (me.height / 2));
            this.setPositionY(me.getCenterY() - (me.height / 2));
        } else if (radAngle > 270 & radAngle < 350) {
            this.setPositionY(me.getCenterY() - (me.height / 2));
            //this.target_Y = enemy.getCenterY() - (enemy.height / 2);
        }
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
        gc.drawImage(this.getImage(), positionX, positionY);
    }

    public Circle getCircle(double radius) {
        return new Circle(positionX + (this.width / 2), positionY + (this.height / 2), radius);
    }

    //Возврат границ прямоугольника
    public Rectangle2D getBoundary() {
        return new Rectangle2D(this.positionX, this.positionY,
                this.width + 30, this.height + 30);
    }

    //-------------------------------------------------------------------------- 
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
        double speed = 25;

        deltaX = target_X - this.getPositionX();
        deltaY = target_Y - this.getPositionY();
        direction = Math.atan(deltaY / deltaX);

        //Если стрельба по юниту
        if (this.enemy != null) {
            //Наносим урон юниту
            for (Unit unit : MyGame.units) {
                if (this.enemy.equals(unit)) {
                    if (unit.getBoundary().intersects(this.getBoundary())) {
                        unit.Health = unit.Health - 300;
                        this.death();
                    }
                }
            }
        } //Если стрельба по зданию
        else if (this.enemyB != null) {
            //Наносим урон зданию
            for (Building bb : MyGame.buildings) {
                if (this.enemyB.equals(bb)) {
                    if (bb.getBoundary().intersects(this.getBoundary())) {
                        bb.Health = bb.Health - 100;
                        this.death();
                    }
                }
            }
        }

        if (this.getCircle(40.0).contains(target_X, target_Y)) {
            //this.death();
            //System.out.println("BINGO " + count); 
        } else if ((deltaX < -0.1 & deltaY < -0.1)) {
            this.setPositionX(this.getPositionX() - (speed * Math.cos(direction)));
            this.setPositionY(this.getPositionY() - (speed * Math.sin(direction)));
        } else if ((deltaX > 0.1 & deltaY > 0.1)) {
            this.setPositionX(this.getPositionX() + (speed * Math.cos(direction)));
            this.setPositionY(this.getPositionY() + (speed * Math.sin(direction)));
        } else if ((deltaX > 0.1 & deltaY < -0.1)) {
            this.setPositionX(this.getPositionX() + (speed * Math.cos(direction)));
            this.setPositionY(this.getPositionY() + (speed * Math.sin(direction)));
        } else if ((deltaX < -0.1 & deltaY > 0.1)) {
            this.setPositionX(this.getPositionX() - (speed * Math.cos(direction)));
            this.setPositionY(this.getPositionY() - (speed * Math.sin(direction)));
        }
    }

    //Уничтожить пулю
    public void death() {
        is_death = true;
    }
}
