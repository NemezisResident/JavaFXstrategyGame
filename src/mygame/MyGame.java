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

    //Динамические элементы отображения
    private Label resourse_lbl;

    //Переменные
    private double target_X;
    private double target_Y;
    private double construkt_X;
    private double construkt_Y;
    private boolean is_construkt;
    private boolean is_select;
    public static double resourse = 100000;
    private long time_pres;
    private long time_drag;

    //Select
    private final Building select = new Building("select");
    private Building select_build;
    private final ArrayList<Unit> selected_sprite = new ArrayList<Unit>();

    //Справочники
    public static ArrayList<Unit> units = new ArrayList<Unit>();
    public static ArrayList<Building> buildings = new ArrayList<Building>();

    //Типы зданий и юнитов    
    private final String unit_type = "unit";
    private final String harvester = "HARVESTER";
    private final String refinary = "REFINARY";
    private final String resources = "RESOURCES";
    private final String medium_plant = "MEDIUM_PLANT";
    private final String base = "BASE";

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
        hbox.getChildren().addAll(create_factory, create_unit, resourse_lbl, cb, cb1, cb2);

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
            q2 += 300;
            //Building res = new Building("res");            
            buildings.add(new Building(resources));//.setPosition(target_X, target_Y));
            buildings.get(buildings.size() - 1).setPosition(q1, q2);
            buildings.get(buildings.size() - 1).setStatus_stranger(0);
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
                    buildings.get(0).setPositionX(construkt_X);
                    buildings.get(0).setPositionY(construkt_Y);
                    buildings.get(0).render(gc);
                }

                //Селект
                if (is_select) {
                    double width = construkt_X - target_X;
                    double hight = construkt_Y - target_Y;
                    select.setPositionX(target_X);
                    select.setPositionY(target_Y);
                    select.render(gc, width, hight);
                }

                //Движение юнитов и пуль                
                for (int i = 0; i < units.size(); i++) {
                    units.get(i).runner();

                    for (Bullet bul : units.get(i).bullet_arr) {
                        double posX = bul.getPositionX();
                        double posY = bul.getPositionY();
                        bul.runner();
                        if (posX == bul.getPositionX() | posY == bul.getPositionY()) {
                            bul.is_death = true;
                        }
                    }
                }

                //Проверка коллизий для пересечения юнитов
                for (int i = 0; i < units.size(); i++) {
                    for (int j = 0; j < buildings.size(); j++) {
                        //Если есть пересечение юнита и здания то
                        if (buildings.get(j).intersects(units.get(i))) {
                            //System.out.println("colliz" + friend_unit.indexOf(friend_unit.get(i)) + " " + friend_unit.get(i).getTarget_X() + " " + friend_unit.get(i).getTarget_Y());                            
                            //Если сборщик на ресурсах
                            if (buildings.get(j).type.equals(resources) & units.get(i).type.equals(harvester)) {
                                Date dt = new Date();
                                if (dt.getTime() - units.get(i).getTime_res() > 500
                                        | units.get(i).getTime_res() == 0) {
                                    units.get(i).harvest(buildings.get(j));
                                    units.get(i).setTime_res(dt.getTime());
                                }
                            } //Если сборщик на заводе
                            else if (buildings.get(j).type.equals(refinary) & units.get(i).type.equals(harvester)) {
                                if (units.get(i).tanker > 0) {
                                    //    Date dt = new Date();
                                    //if (dt.getTime() - friend_unit.get(i).getTime_shot() > 500
                                    // | friend_unit.get(i).getTime_shot() == 0) {

                                    units.get(i).unload_res();
                                    //friend_unit.get(i).setTime_shot(dt.getTime());
                                    // }
                                    //  else{
                                    //   System.out.println("bug");
                                } //}
                                else if (units.get(i).tanker <= 0) {
                                    units.get(i).get_res_target();
                                }
                            }
                        }
                    }
                }

                //Проверка радиуса обзора для стрельбы
                for (int i = 0; i < units.size(); i++) {
                    //Проверяем юнитов в радиусе
                    for (int j = 0; j < units.size(); j++) {
                        if (units.get(i).intersects_enemy(units.get(j))) {
                            if (units.get(i).getStatus_stranger() == 1 & units.get(j).getStatus_stranger() == 2
                                    | units.get(i).getStatus_stranger() == 2 & units.get(j).getStatus_stranger() == 1) {
                                if (units.get(i).type.equals(unit_type)) {
                                    Date dt = new Date();
                                    if (dt.getTime() - units.get(i).getTime_shot() > 500
                                            | units.get(i).getTime_shot() == 0) {
                                        units.get(i).shot(units.get(i), units.get(j));
                                        units.get(i).setTime_shot(dt.getTime());
                                    }
                                }
                            }
                        }
                    }
                    //Проверяем здания в радиусе
                    for (int k = 0; k < buildings.size(); k++) {
                        if (units.get(i).intersects_enemy_b(buildings.get(k))) {
                            if (units.get(i).getStatus_stranger() == 1 & buildings.get(k).getStatus_stranger() == 2
                                    | units.get(i).getStatus_stranger() == 2 & buildings.get(k).getStatus_stranger() == 1) {
                                if (units.get(i).type.equals(unit_type)) {
                                    Date dt = new Date();
                                    if (dt.getTime() - units.get(i).getTime_shot() > 500
                                            | units.get(i).getTime_shot() == 0) {
                                        units.get(i).shot(units.get(i), buildings.get(k));
                                        units.get(i).setTime_shot(dt.getTime());
                                    }
                                }
                            }
                        }
                    }
                }

                //--------------------------------------------------------------
                //Отрисовка дружеских зданий          
                for (int i = 0; i < buildings.size(); i++) {
                    buildings.get(i).render(gc);
                    //Если исчерпали ресурсы то дальше                
                    if (buildings.get(i).Health <= 0) {
                        buildings.remove(buildings.get(i));
                        for (int k = 0; k < units.size(); k++) {
                            if (units.get(k).type.equals(harvester)) {
                                if (units.get(k).tanker < units.get(k).limit_tanker) {
                                    units.get(k).get_res_target();
                                }
                            }
                        }
                    }
                }

                //Отрисовка юнитов          
                for (int j = 0; j < units.size(); j++) {
                    units.get(j).render(gc);
                    for (int i = 0; i < units.get(j).bullet_arr.size(); i++) {
                        units.get(j).bullet_arr.get(i).render(gc);
                        if (units.get(j).bullet_arr.get(i).is_death) {
                            units.get(j).bullet_arr.remove(units.get(j).bullet_arr.get(i));
                        }
                    }
                    //Уничтожение
                    if (units.get(j).Health <= 0) {
                        units.remove(units.get(j));
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
                    buildings.get(0).setImage(new Image("img/refinary.png"));
                    //Перенести в конструктор
                    Unit un = new Unit(harvester);
                    un.setImage(new Image("img/Harvester.png"));
                    un.setCenterX(event.getX());
                    un.setCenterY(event.getY());
                    un.setBase_jps_x(event.getX());
                    un.setBase_jps_y(event.getY());
                    un.get_res_target();
                    un.speed = 1;
                    if (cb.selectedProperty().get()) {
                        un.setStatus_stranger(2);
                    } else {
                        un.setStatus_stranger(1);
                    }
                    units.add(un);
                } else if (cb2.selectedProperty().get()) {
                    buildings.get(0).setImage(new Image("img/base.png"));
                } else {
                    buildings.get(0).setImage(new Image("img/base4.png"));
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
                for (int i = 0; i < units.size(); i++) {
                    if (select.intersects(units.get(i), construkt_X, construkt_Y)) {
                        //Если наш юнит
                        //if (units.get(i).getStatus_stranger()==1){
                        selected_sprite.add(units.get(i));
                        //}
                    }
                }
            }

            //Если выбран один юнит то
            if (!is_select) {
                for (int i = 0; i < units.size(); i++) {
                    if (units.get(i).getBoundary().contains(event.getX(), event.getY())) {
                        //Если наш юнит
                        //if (units.get(i).getStatus_stranger()==1){
                        selected_sprite.clear();
                        select_build = null;
                        selected_sprite.add(units.get(i));
                        return;
                        //}
                    }
                }
                //Выбранное здание
                for (int i = 0; i < buildings.size(); i++) {
                    if (buildings.get(i).getBoundary().contains(event.getX(), event.getY())) {
                        //Если наш юнит
                        //if (buildings.get(i).getStatus_stranger()==1){                        
                        //Если очистка или ресурсы
                        if (buildings.get(i).type.equals(resources) | buildings.get(i).type.equals(refinary)) {
                            if (selected_sprite.get(0).type.equals(harvester)) {
                                selected_sprite.get(0).setTarget_X(event.getX());
                                selected_sprite.get(0).setTarget_Y(event.getY());
                                return;
                            }
                        }
                        selected_sprite.clear();
                        select_build = buildings.get(i);
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
                    build = new Building(refinary);
                } else if (cb2.selectedProperty().get()) {
                    build = new Building(medium_plant);
                } else {
                    build = new Building(base);
                }
                //Если построить вражеское здание
                if (cb.selectedProperty().get()) {
                    build.setStatus_stranger(2);
                } else {
                    build.setStatus_stranger(1);
                    resourse = resourse - 25000;
                }
                buildings.add(0, build);
            } else {
                System.out.println("NO resourse");
            }
        });

        //Создать юнита
        create_unit.setOnAction(event -> {
            //Проверка бабок
            if (resourse - 1000 >= 0) {
                if (select_build != null){ 
                    if (select_build.type.equals(medium_plant)) {
                    Unit unit = new Unit(unit_type, select_build.getPositionX() + 110, select_build.getPositionY() + 160);
                    //Если строим врага
                    if (cb.selectedProperty().get()) {
                        unit.setStatus_stranger(2);
                    } else {
                        unit.setStatus_stranger(1);
                        resourse = resourse - 1000;
                    }
                    units.add(0, unit);
                    }
                } else {
                    System.out.println("Не выбран завод");
                    return;
                }
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
