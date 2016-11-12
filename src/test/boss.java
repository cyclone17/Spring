package test;

public class boss {
  private office office;
  private car car;
  //@Autowired
  //public boss(car car ,office office){
  //    this.car = car;
   //   this.office = office ;
  //}
  
  	public office getoffice(){
  		return office;
  	} 
  	public void setoffice(office office){
  		this.office = office;
  	}
  	public car getcar(){
  		return car;
  	}
  	public void setcar(car car){
  		this.car=car;
  	}

  public String tostring(){
	  return "this boss has "+car.tostring()+"and in "+office.tostring();
  }
}
