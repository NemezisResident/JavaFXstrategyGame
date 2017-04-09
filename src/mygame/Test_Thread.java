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
public class Test_Thread extends Thread {
    
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
                
                //Движение юнитов и пуль                
                for (int i = 0; i < units.size(); i++) {
                    units.get(i).runner();
                    for (int j = 0; j < units.get(i).bullet_arr.size(); j++) {
                        double posX = units.get(i).bullet_arr.get(j).getPositionX();
                        double posY = units.get(i).bullet_arr.get(j).getPositionY();
                        units.get(i).bullet_arr.get(j).runner();
                        if (posX == units.get(i).bullet_arr.get(j).getPositionX() | posY == units.get(i).bullet_arr.get(j).getPositionY()) {
                            units.get(i).bullet_arr.get(j).is_death = true;
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
                 
            } catch (Exception ex) {
               ex.printStackTrace();
            }
        }
    }
}
