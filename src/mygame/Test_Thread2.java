/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import java.util.Date;
import static mygame.MyGame.buildings;
import static mygame.MyGame.units;
import sprite.Bullet;




/**
 *
 * @author Nemezis
 */
public class Test_Thread2 extends Thread {
    
    //Типы зданий и юнитов    
    private final String unit_type = "unit";
    private final String harvester = "HARVESTER";
    private final String refinary = "REFINARY";
    private final String resources = "RESOURCES";
    private final String medium_plant = "MEDIUM_PLANT";
    private final String base = "BASE";
    
    
    public void run() {
        while(true){
            try {
                Thread.sleep(15);
                
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
                                        // System.out.println(units.get(i).getTime_shot());
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
                 
            } catch (Exception ex) {
               ex.printStackTrace();
            }
        }
    }
}
