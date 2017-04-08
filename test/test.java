
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nemezis
 */
public class test {

    public static void main(String[] args) {

        for (int i = 0; i < 100; i++) {

        Random randNumber = new Random();
        double iNumber = randNumber.nextDouble();
        System.out.println(iNumber);
    }
}
}
