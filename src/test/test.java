package test;
import test.BeanFactory;
public class test {

    public static void main(String[] args) {
    	BeanFactory f = new BeanFactory();
    	f.init("src/bean.xml");
        
    	//String[] locations = {"bean.xml"};
    	boss boss = (boss) f.getBean("boss");
    	//office office = (office) f.getBean("office");
    	//car car = (car) f.getBean("car");
        //ApplicationContext ctx = new ClassPathXmlApplicationContext(locations);
    	System.out.println(boss.getoffice()+"\n" + boss.getcar());
    	 // boss boss = (boss) ctx.getBean("boss");
    	//System.out.println("userNcame=" + office.getOfficeId());
    	System.out.println("this boss has "+"the car is " +boss.getcar().getCarColor()+" with " +boss.getcar().getCarId()+" and in office " +boss.getoffice().getOfficeId());
    	//System.out.println("userNcame=" + car.getCarColor());
    	
    	boss.tostring();
    }
}