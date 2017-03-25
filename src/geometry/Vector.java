/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geometry;

/**
 *
 * @author Nemezis
 */
public class Vector {

    private double A;
    private double B;

    //Находим вектор по координатам 2 точек
    public Vector(double x_A, double y_A, double x_B, double y_B) {
        this.A = x_B - x_A;
        this.B = y_B - y_A;
    }

    //Установка и получение
    public double getA() {
        return A;
    }

    public void setA(double A) {
        this.A = A;
    }

    public double getB() {
        return B;
    }

    public void setB(double B) {
        this.B = B;
    }

    //Метод расчета угла поворота
    public static Double getAngle(Vector A, Vector B) {

        double scalPR = A.getA() * B.getA() + A.getB() * B.getB();
        double modA = Math.sqrt(Math.pow(A.getA(), 2) + Math.pow(A.getB(), 2));
        double modB = Math.sqrt(Math.pow(B.getA(), 2) + Math.pow(B.getB(), 2));

        double cos = scalPR / (modA * modB);

        double RAD = Math.acos(cos);
        double radAngle = RAD * 180 / Math.PI;

        return radAngle;
    }
}
