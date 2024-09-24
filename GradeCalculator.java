import java.util.Scanner;
public class GradeCalculator{
    
    //Weight for each grade
    final private static double PA = 0.5;   //This allow you to apply to eveyrhting thing
    final private static double MT = 0.125;
    final private static double FI = 0.375;

    public static void main(String[] args){
        
        //Prompt the user to enter the scores
        Scanner keyin = new Scanner(System.in);
        int PAs = keyin.nextInt();
        if (PAs <= 0){
            System.out.println("invalid input");
            return;
        }

        int PAtotal = 0;
        for (int i = 0; i < PAs; ++i){
            int PAgrade = keyin.nextInt();
            if (PAgrade < 0 || PAgrade > 100){
                System.out.println("invalid input");
                return;  
            }
            else {
                PAtotal += PAgrade;
            }
        }
        double PAavg = (double) PAtotal / PAs;

        int MTtotal = keyin.nextInt();
        int FIscore = keyin.nextInt();
        keyin.close();
        if (MTtotal < 0 || MTtotal > 100){
            System.out.println ("invalid input");
            return;
        }
        if (FIscore < 0||FIscore > 100){
            System.out.println ("invalid input");
            return;
        }

        //Grade Total
        double FinalScore = (PAavg * PA) + (MTtotal * MT) + (FIscore * FI);
        

        //Letter grade
        char grade;
        if (FinalScore >= 90){
            grade = 'A';
        }
        else if (FinalScore >= 80){
            grade = 'B';
        }
        else if (FinalScore >= 70){
            grade = 'C';
        }
        else if (FinalScore >= 60){
            grade = 'D';
        }
        else{
            grade = 'F';
        }
        System.out.println(FinalScore);
        System.out.println(grade);
    }
}

