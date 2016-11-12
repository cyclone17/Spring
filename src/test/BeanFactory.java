package test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;

public class BeanFactory {
	
	private Map<String,Object> beanMap=new HashMap<String,Object>();
	
	//��ʼ��xml�ļ�
	public void init(String xmlUrl){
		SAXReader  saxReader=new SAXReader();
		File file=new File(xmlUrl);
		try{
			saxReader.addHandler("/beans/bean",new BeanHandler());
			saxReader.read(file);
		}
		catch(DocumentException e){
			System.out.println(e.getMessage());
		}
	}
	

	
	private void setFieldValue(Object obj, Field field, String value) {
		String fieldType=field.getType().getSimpleName();
		try{
			if("int".equals(fieldType)){
				field.setInt(obj, new Integer(value));
			}else{
				field.set(obj,value);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void setFieldValue(Object obj, Field field, Object bean) {
		try {
			field.set(obj, bean);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	
/*************************�����Ǳ�ǩ������***************************************/
	
	//����/beans/bean 	 
	class BeanHandler implements ElementHandler {

		private Object obj=null;
		
		public void onStart(ElementPath path) {
			
			Element beanElement=path.getCurrent();      //��ȡ��ǰ��Ԫ��
			Attribute classAttribute=beanElement.attribute("class");  //��ȡclass����
			
			Class<?> bean=null;
			
			try{
				bean=Class.forName(classAttribute.getText()); //��������ͨ�����乹��һ��class
			}
			catch(ClassNotFoundException e){
				e.printStackTrace();
			}
			
			Field fields[]=bean.getDeclaredFields();//��ȡbean�е����е���
			Map<String,Field> fieldMap=new HashMap<String,Field>();
			
			//���е������һ��map��
			for(Field field:fields){
				fieldMap.put(field.getName(), field);
			}
			
			try{
				obj=bean.newInstance(); //�����һ��beanʵ������
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			//Ϊproperty ����handler
			path.addHandler("property", new PropertyHandler(fieldMap,obj));
		}
		public void onEnd(ElementPath path) {
			Element currentElement=path.getCurrent();
			Attribute idAttribute=currentElement.attribute("id");
			beanMap.put(idAttribute.getText(), obj);		
			//ȥ��property������
			path.removeHandler("property");		
		}
	}
	
	//����property 
	class PropertyHandler implements ElementHandler {
		
		private Map<String,Field> fieldMap;
		private Object obj;
		
		public PropertyHandler(Map<String,Field> fieldMap,Object obj){
			this.fieldMap=fieldMap;
			this.obj=obj;		
		}

		public void onEnd(ElementPath path) {
			path.removeHandler("value");
			path.removeHandler("ref");
		}

		public void onStart(ElementPath path) {
			Element propertyElement=path.getCurrent();
			Attribute nameAttribute=propertyElement.attribute("name");
			path.addHandler("value", new ValueHandler(fieldMap,obj,nameAttribute));
			path.addHandler("ref", new RefHandler(fieldMap,obj,nameAttribute));

		}
	}
	
	//����value
	class ValueHandler implements ElementHandler{
		
		private Map<String,Field> fieldMap;
		private Object obj;
		private Attribute nameAttribute;
		
		public ValueHandler(Map<String,Field> fieldMap,Object obj,Attribute nameAttribute){
			this.fieldMap=fieldMap;
			this.obj=obj;
			this.nameAttribute=nameAttribute;		
		}

		public void onEnd(ElementPath path) {
			Element valueElement=path.getCurrent();
			String strValue=valueElement.getText();
			Field tempField=fieldMap.get(nameAttribute.getValue());
			if(tempField!=null){
				tempField.setAccessible(true);
				setFieldValue(obj,tempField,strValue);//����bean�����Ե�ֵ
			}
		}
		public void onStart(ElementPath arg0) {
		}
	}

	// ����ref
	class RefHandler implements ElementHandler{
		
		private Map<String,Field> fieldMap;
		private Object obj;
		private Attribute nameAttribute;
		private Object bean;
		
		public RefHandler(Map<String,Field> fieldMap,Object obj,Attribute nameAttribute){
			this.fieldMap=fieldMap;
			this.obj=obj;
			this.nameAttribute=nameAttribute;	
		}

		public void onEnd(ElementPath arg0) {
			Field tempField=fieldMap.get(nameAttribute.getValue());
			if(tempField!=null){
				tempField.setAccessible(true);				
				setFieldValue(obj,tempField,bean);//����bean�����Ե�ֵ
			}			
		}
		public void onStart(ElementPath path) {
			Element refElement=path.getCurrent();
			Attribute beanAttribute=refElement.attribute("bean");
			bean=getBean(beanAttribute.getValue());
		}		
	}
	
	//����beanName����ȡbean
	public Object getBean(String beanName){
		Object obj=beanMap.get(beanName);
		return obj;		
	}
}
