/*
 * To change spr license header, choose License Headers in Project Properties.
 * To change spr template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import sprite.Unit;
import sprite.Bullet;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import sprite.Building;

/**
 *
 * @author Nemezis
 */
public class MyGame extends Application {

    //Переменные
    private double target_X;
    private double target_Y;
    private double construkt_X;
    private double construkt_Y;
    private boolean is_construkt;
    private boolean is_select;
    private final String unit_type = "unit";
    private int count;
    public static double resourse = 100000;
    private long time_pres;
    private long time_drag;

    //Select
    private final Building select = new Building("select");
    private Building select_build;
    private final ArrayList<Unit> selected_sprite = new ArrayList<Unit>();

    //Справочники
    public static ArrayList<Unit> friend_unit = new ArrayList<Unit>();
    public static ArrayList<Building> friend_build = new ArrayList<Building>();
    public static ArrayList<Unit> enemy_unit = new ArrayList<Unit>();

    //Динамические элементы
    private Label resourse_lbl;
    //--------------------------------------------------------------------------

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage theStage) {
        theStage.setTitle("Game robotics");
        Group root = new Group();
        ScrollPane scrol = new ScrollPane();
        scrol.setPrefSize(1500, 900);
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        vbox.getChildren().addAll(hbox, scrol);
        CheckBox cb = new CheckBox("Враг");
        cb.setAllowIndeterminate(false);
        CheckBox cb1 = new CheckBox("Ресурсы");
        cb1.setAllowIndeterminate(false);
        CheckBox cb2 = new CheckBox("Mech");
        cb2.setAllowIndeterminate(false);

        Button create_unit = new Button("СОЗДАТЬ");
        Button create_factory = new Button("Построить");
        resourse_lbl = new Label(String.valueOf(resourse));
        hbox.getChildren().addAll(create_factory, create_unit, resourse_lbl, cb, cb1,cb2);

        //
        Scene theScene = new Scene(vbox);
        theStage.setScene(theScene);

        //Карта раунда
        final ImageView imv = new ImageView();
        final Image image2 = new Image("img/grac.png");
        imv.setImage(image2);
        root.getChildren().add(imv);

        //Canvas
        Canvas canvas = new Canvas(3000, 3000);
        root.getChildren().add(canvas);
        scrol.setContent(root);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //Sprite       
        select.setImage(new Image("img/select.png"));
      
        //Создаем ресурсы
        int q1 = 50;
        int q2 = 0;
        
        for (int i = 0; i < 10; i++) {            
            q2 +=300; 
            //Building res = new Building("res");            
            friend_build.add(new Building("res"));//.setPosition(target_X, target_Y));
            friend_build.get(friend_build.size()-1).setPosition(q1, q2);
        }

        //Loop
        Timeline gameLoop = new Timeline();
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        final long timeStart = System.currentTimeMillis();
        KeyFrame kf = new KeyFrame(
                Duration.seconds(0.017), // 60 FPS    
                new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                //------------------------------------------------------------------
                //Цикл игры  
                // Clear the canvas
                gc.clearRect(0, 0, 3000, 3000);

                //Задний фон
                //gc.setFill(Color.SILVER);
                //gc.fillRect(0, 0, 3000, 3000);
                //gc.drawImage(ref, 100, 100 );
                //gc.drawImage(earth, x, y);
                //double t = (System.currentTimeMillis() - timeStart) / 1000.0;
                //double x = 400 + 128 * Math.cos(t);
                //double y = 400 + 128 * Math.sin(t);
                //Показываем ресурсы
                setResourse_lbl(String.valueOf(resourse));

                //Строительство
                if (is_construkt) {
                    friend_build.get(0).setPositionX(construkt_X);
                    friend_build.get(0).setPositionY(construkt_Y);
                    friend_build.get(0).render(gc);
                }

                //Селект
                if (is_select) {
                    double width = construkt_X - target_X;
                    double hight = construkt_Y - target_Y;
                    select.setPositionX(target_X);
                    select.setPositionY(target_Y);
                    select.render(gc, width, hight);
                }

                //Движение дружеских юнитов и пуль                
                for (int i = 0; i < friend_unit.size(); i++) {
                    friend_unit.get(i).runner();
                    
                    System.out.println( "type " + friend_unit.get(i).type + 
                            " un " + friend_unit.indexOf(friend_unit.get(i)) 
                            + " tanker " + friend_unit.get(i).tanker 
                            + " target x " + friend_unit.get(i).target_X 
                            + " y " + friend_unit.get(i).target_Y);
                    
                    for (Bullet bul : friend_unit.get(i).bullet_arr) {
                        double posX = bul.getPositionX();
                        double posY = bul.getPositionY();
                        bul.runner();
                        if (posX == bul.getPositionX() | posY == bul.getPositionY()) {
                            bul.is_death = true;
                        }
                    }
                }
                
                 //Движение вражеских юнитов и пуль
                for (Unit sprite : enemy_unit) {
                    //sprite.runner();
                    for (Bullet bul : sprite.bullet_arr) {
                        double posX = bul.getPositionX();
                        double posY = bul.getPositionY();
                        bul.runner();
                        if (posX == bul.getPositionX() | posY == bul.getPositionY()) {
                            bul.is_death = true;
                        }
                    }
                }

                //Проверка коллизий для пересечения юнитов
                for (int i = 0; i < friend_unit.size(); i++) {
                    for (int j = 0; j < friend_build.size(); j++) {                        
                        
                        //Если есть пересечение юнита и здания то
                        if (friend_build.get(j).intersects(friend_unit.get(i))) {
                            //System.out.println("colliz" + friend_unit.indexOf(friend_unit.get(i)) + " " + friend_unit.get(i).getTarget_X() + " " + friend_unit.get(i).getTarget_Y());                            
                            //Если сборщик на ресурсах
                            if (friend_build.get(j).type.equals("res") & friend_unit.get(i).type.equals("harvest")) {

                                Date dt = new Date();
                                if (dt.getTime() - friend_unit.get(i).getTime_res() > 500
                                        | friend_unit.get(i).getTime_res() == 0) {
                                    friend_unit.get(i).harvest(friend_build.get(j));
                                    friend_unit.get(i).setTime_res(dt.getTime());
                                }                                  
                            }
                            
                            //Если сборщик на заводе
                            else if (friend_build.get(j).type.equals("refinary") & friend_unit.get(i).type.equals("harvest")) {                                                                
                                if (friend_unit.get(i).tanker > 0) {
                                //    Date dt = new Date();
                                    //if (dt.getTime() - friend_unit.get(i).getTime_shot() > 500
                                           // | friend_unit.get(i).getTime_shot() == 0) {

                                        friend_unit.get(i).unload_res();
                                        //friend_unit.get(i).setTime_shot(dt.getTime());
                                   // }
                                  //  else{
                                     //   System.out.println("bug");
                                    }
                                //}
                                else if (friend_unit.get(i).tanker <= 0) {
                                   friend_unit.get(i).get_res_target();
                                }
                            }
                        }
                    }
                }

                //Проверка коллизий для стрельбы
                for (int i = 0; i < friend_unit.size(); i++) {
                    for (int j = 0; j < enemy_unit.size(); j++) {
                        if (friend_unit.get(i).intersects_enemy(enemy_unit.get(j))) {
                            if (friend_unit.get(i).type.equals(unit_type)) {
                                Date dt = new Date();
                                if (dt.getTime() - friend_unit.get(i).getTime_shot() > 500
                                        | friend_unit.get(i).getTime_shot() == 0) {
                                    friend_unit.get(i).shot(friend_unit.get(i), enemy_unit.get(j));
                                    friend_unit.get(i).setTime_shot(dt.getTime());
                                }
                            }

                            //Стрельба врага
                            Date dt = new Date();
                            if (dt.getTime() - enemy_unit.get(j).getTime_shot() > 500
                                    | enemy_unit.get(j).getTime_shot() == 0) {
                                enemy_unit.get(j).shot(enemy_unit.get(j), friend_unit.get(i));
                                enemy_unit.get(j).setTime_shot(dt.getTime());
                            }
                        }
                    }
                }
                
                //--------------------------------------------------------------
                //Отрисовка дружеских зданий          
                for (int i = 0; i < friend_build.size(); i++) {
                    friend_build.get(i).render(gc);
                    //Если исчерпали ресурсы то дальше                
                    if (friend_build.get(i).Health <= 0) {
                        friend_build.remove(friend_build.get(i));
                        for (int k = 0; k < friend_unit.size(); k++) {
                            if (friend_unit.get(k).type.equals("harvest")) {
                                if (friend_unit.get(k).tanker < friend_unit.get(k).limit_tanker) {
                                    friend_unit.get(k).get_res_target();

                                }
                            }
                        }
                    }
                }

                //Отрисовка дружеских юнитов          
                for (int j = 0; j < friend_unit.size(); j++) {
                    friend_unit.get(j).render(gc);
                    for (int i = 0; i < friend_unit.get(j).bullet_arr.size(); i++) {
                        friend_unit.get(j).bullet_arr.get(i).render(gc);
                        if (friend_unit.get(j).bullet_arr.get(i).is_death) {
                            friend_unit.get(j).bullet_arr.remove(friend_unit.get(j).bullet_arr.get(i));
                        }
                    }
                    //Уничтожение
                    if (friend_unit.get(j).Health <= 0) {
                        friend_unit.remove(friend_unit.get(j));
                    }
                }

                //Отрисовка вражеских юнитов
                for (int i = 0; i < enemy_unit.size(); i++) {
                    enemy_unit.get(i).render(gc);

                    for (int j = 0; j < enemy_unit.get(i).bullet_arr.size(); j++) {
                        enemy_unit.get(i).bullet_arr.get(j).render(gc);
                        if (enemy_unit.get(i).bullet_arr.get(j).is_death) {
                            enemy_unit.get(i).bullet_arr.remove(enemy_unit.get(i).bullet_arr.get(j));
                        }
                    }

                    if (enemy_unit.get(i).Health <= 0) {
                        enemy_unit.remove(enemy_unit.get(i));
                    }
                }
            }
        });

        //Обработчики методы----------------------------------------------------
        //Следим за движением мыши
        root.setOnMouseMoved(event -> {
            construkt_X = event.getX();
            construkt_Y = event.getY();
            //System.out.println(construkt_X + " " + construkt_Y);
        });

        //Тащим
        root.setOnMouseDragged(event -> {
            Date dt = new Date();
            time_drag = dt.getTime();
            if (time_drag - time_pres > 150) {
                construkt_X = event.getX();
                construkt_Y = event.getY();
                is_select = true;
            }
        });

        //Нажали
        root.setOnMousePressed(event -> {
            target_X = event.getX();
            target_Y = event.getY();
            Date dt = new Date();
            time_pres = dt.getTime();
        });

        //Отпустили кнопку мыши
        root.setOnMouseReleased(event -> {
            //Если режим конструктора
            if (is_construkt) {
                if (cb1.selectedProperty().get()) {
                    friend_build.get(0).setImage(new Image("img/refinary.png"));
                    //Перенести в конструктор
                    Unit un = new Unit("harvest");
                    un.setImage(new Image("img/Harvester.png"));
                    un.setCenterX(event.getX());
                    un.setCenterY(event.getY());
                    un.setBase_jps_x(event.getX());
                    un.setBase_jps_y(event.getY());
                    un.get_res_target();
                    un.speed = 1;
                    friend_unit.add(un);
                }
                else if (cb2.selectedProperty().get()) {
                    friend_build.get(0).setImage(new Image("img/base.png"));
                } else {
                    friend_build.get(0).setImage(new Image("img/base4.png"));
                }
                //Очищаем все селектные списки
                select_build = null;
                selected_sprite.clear();
                is_construkt = false;
                return;
            } else if (is_select) {
                select_build = null;
                selected_sprite.clear();
            }

            //Если режим селекта
            if (is_select) {                
                for (int i = 0; i < friend_unit.size(); i++) {
                    if (select.intersects(friend_unit.get(i))) {
                        selected_sprite.add(friend_unit.get(i));
                    }
                }
            }

            //Если выбран один юнит то
            if (!is_select) {
                for (int i = 0; i < friend_unit.size(); i++) {
                    if (friend_unit.get(i).getBoundary().contains(event.getX(), event.getY())) {
                        selected_sprite.clear();
                        select_build = null;
                        selected_sprite.add(friend_unit.get(i));
                        return;
                    }
                }
                //Выбранное здание
                for (int i = 0; i < friend_build.size(); i++) {
                    if (friend_build.get(i).getBoundary().contains(event.getX(), event.getY())) {                        
                        
                        //Если очистка или ресурсы
                        if (friend_build.get(i).type.equals("res")|friend_build.get(i).type.equals("refinary") ) {
                            if (selected_sprite.get(0).type.equals("harvest")) {
                                selected_sprite.get(0).setTarget_X(event.getX());
                                selected_sprite.get(0).setTarget_Y(event.getY());
                                return;
                            }
                        }
                        selected_sprite.clear();
                        select_build = friend_build.get(i);
                        return;
                    }
                }
            }

            //Если выбрано несколько юнитов
            if (!is_select) {
                int iterator_x = 0;
                int iterator_y = 0;

                //Находим ребро квадрата
                double d = (double) selected_sprite.size();
                d = Math.sqrt(d);

                //Если выбран только один юнит то
                if (selected_sprite.size() == 1) {
                    selected_sprite.get(0).setTarget_X(event.getX());
                    selected_sprite.get(0).setTarget_Y(event.getY());
                }

                //Разбивка юнитов на карте по сетке
                for (int i = 1; i < selected_sprite.size(); i++) {
                    selected_sprite.get(i - 1).setTarget_X(event.getX() + iterator_x);
                    selected_sprite.get(i - 1).setTarget_Y(event.getY() + iterator_y);
                    iterator_x = iterator_x + (int) selected_sprite.get(0).width;

                    //Если длиннее стороны квадрата то переходим на следующую строку
                    if (i - 1 != 0 & i % (int) d == 0) {
                        iterator_x = 0;
                        iterator_y = iterator_y + (int) selected_sprite.get(0).height;
                    }
                    //Если последний юнит то 
                    if (i == selected_sprite.size() - 1) {
                        selected_sprite.get(i).setTarget_X(event.getX() + iterator_x);
                        selected_sprite.get(i).setTarget_Y(event.getY() + iterator_y);
                    }
                }
                select_build = null;
            }
            
            is_select = false;
        });

        //События кнопок--------------------------------------------------------
        //Обработчик кнопки создание зданий
        create_factory.setOnAction(event -> {
            if (resourse - 25000 >= 0) {
                is_construkt = true;
                //Создаем базу
                Building build;
                //Если очистку
                if (cb1.selectedProperty().get()) {
                    build = new Building("refinary");
                } 
                else if (cb2.selectedProperty().get()) {
                    build = new Building("factory");
                }                  
                else {
                    build = new Building("base");
                }
                friend_build.add(0, build);
                resourse = resourse - 25000;
            } else {
                System.out.println("NO resourse");
            }
        });

        //Создать юнита
        create_unit.setOnAction(event -> {
            //Создание вражеского юнита
            if (cb.selectedProperty().get()) {
                Unit unit = new Unit(unit_type);
                unit.setCenterX(600);
                unit.setCenterY(600);
                enemy_unit.add(0, unit);
                return;
            }
            //Проверка бабок
            if (resourse - 500 >= 0) {
                if (select_build != null) {
                    if (select_build.type.equals("factory")) {
                        Unit unit = new Unit(unit_type, select_build.getPositionX() + 110, select_build.getPositionY() + 160);                        
                        friend_unit.add(0, unit);
                    }
                } else {
                    System.out.println("Не выбран завод");
                    return;
                }
                resourse = resourse - 500;
            } else {
                System.out.println("NO resourse");
            }
        });

        //----------------------------------------------------------------------
        gameLoop.getKeyFrames().add(kf);
        gameLoop.play();

        theStage.show();
    }

    public void setResourse_lbl(String text) {
        this.resourse_lbl.setText(text);
    }

    public static double getResourse() {
        return resourse;
    }

    public static void setResourse(double resourse) {
        MyGame.resourse = resourse;
    }
}

//if ( targetData.containsPoint( e.getX(), e.getY() ) )
