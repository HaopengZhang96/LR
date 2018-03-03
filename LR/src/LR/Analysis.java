package LR;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import bean.Derivation;
import bean.Grammar;

public class Analysis {
	boolean flag=true;
	Grammar grammar=new Grammar();  //������ķ�
	List<Grammar> canonical=new ArrayList<Grammar>();  //��Ŀ���淶��
	List<HashMap<String,String>> table=new ArrayList<HashMap<String,String>>();//������ 
	public  void input(){
		Integer n=0;
		Scanner in=new Scanner(System.in);
		System.out.println("�������ķ��������");
		n=in.nextInt();
		System.out.println("�������ķ�");
		List<Derivation> projcetSet=new ArrayList<Derivation>(); //���ķ�����
		List<String> vn=new ArrayList<String>();
		List<String> vt=new ArrayList<String>();
		Derivation extenDer=new Derivation();
		for(int i=0;i<n;i++){
			Derivation der=new Derivation();
			String tempGra=in.next();
			String[] gra=tempGra.split("->");
			der.setLeft(gra[0]);
			der.setRight(gra[1]);
			if(i==0){
				extenDer.setLeft("S'");        //S'->
				extenDer.setRight(gra[0]);
				projcetSet.add(extenDer);
				vn.add("S'");
			}
			if(!projcetSet.contains(der))   //������򼯲���������ķ�
				projcetSet.add(der);
			if(!vn.contains(gra[0]))        //�󲿶��Ƿ��ս����������ս����������������ս��
				vn.add(gra[0]);
			
		}
		
	
		grammar.setProjcetSet(projcetSet);
		grammar.setVn(vn);
		for(int i=0;i<grammar.getProjcetSet().size();i++){
			String rightTemp=grammar.getProjcetSet().get(i).getRight();
			for(int j=0;j<rightTemp.length();j++){   //���Ҳ���ȡ�ս��
				if((!vn.contains(String.valueOf(rightTemp.charAt(j))))&&(!vt.contains(String.valueOf(rightTemp.charAt(j)))))
					vt.add(String.valueOf(rightTemp.charAt(j)));
			}
		}
		grammar.setVt(vt);
		
		
		for(int i=0;i<grammar.getProjcetSet().size();i++){
			System.out.println(grammar.getProjcetSet().get(i).getLeft()+"->"+grammar.getProjcetSet().get(i).getRight());
		}
		System.out.println("Vn>>>>>>>>>>>");
		for(int i=0;i<grammar.getVn().size();i++){
			System.out.println(grammar.getVn().get(i));
		}
		System.out.println("Vt>>>>>>>>>>>");
		for(int i=0;i<grammar.getVt().size();i++){
			System.out.println(grammar.getVt().get(i));
		}
	}
	public Grammar closure(Grammar core){                      //��˵ıհ�
		Grammar gra=new Grammar();
		List<Derivation> projcetSet=new ArrayList<Derivation>();
		List<String> go=new ArrayList<String>();
		for(int i=0;i<core.getProjcetSet().size();i++){        //�ȰѺ���հ���
			projcetSet.add(core.getProjcetSet().get(i));
		}
		for(int i=0;i<projcetSet.size();i++){       //Ѱ��.�����Ƿ��ս������Ŀ
			String right=projcetSet.get(i).getRight();
			for(int j=0;j<right.length();j++){  
				if(String.valueOf(right.charAt(j)).equals(".")){//����ҵ���
					if((j+1)<right.length()){                    //�ѵ����ķ��ŷŵ����ƽ����ż���������Ҫȥ��
						if(!go.contains(String.valueOf(right.charAt(j+1))))
							go.add(String.valueOf(right.charAt(j+1)));
					}
					if((j+1)<right.length()&&grammar.getVn().contains(String.valueOf(right.charAt(j+1)))){  //���һ��Ƿ��ս��
						for(int k=0;k<grammar.getProjcetSet().size();k++){  //�����ķ�����
							if(grammar.getProjcetSet().get(k).getLeft().equals(String.valueOf(right.charAt(j+1)))){ //�ҵ���Ӧ���ķ�
								Derivation temp=new Derivation();
								temp.setLeft(String.valueOf(right.charAt(j+1)));
								temp.setRight("."+grammar.getProjcetSet().get(k).getRight());
								if(!projcetSet.contains(temp))
									projcetSet.add(temp);
							}
						}
					}
				}
			}
		}
		gra.setProjcetSet(projcetSet);
		gra.setGo(go);
		return gra;
	}
	public Grammar go(Grammar I,String X){                     //Go���� GO(I,X)=CLOSURE(J)
		
		Grammar go;
		Grammar core=new Grammar();
		List<Derivation> pro =new ArrayList<Derivation>();
		for(int i=0;i<I.getProjcetSet().size();i++){
			String left=I.getProjcetSet().get(i).getLeft();
			String right=I.getProjcetSet().get(i).getRight();
			for(int j=0;j<right.length();j++){
				if(String.valueOf(right.charAt(j)).equals(".")){//����ҵ���
					if((j+1)<right.length()&&String.valueOf(right.charAt(j+1)).equals(X)){  //.�����Ҫ�ƽ���X
						Derivation temp=new Derivation();
						if(X.equals("(")||X.equals(")")||X.equals("*")||X.equals("+"))
							X="\\"+X;
						String newRight=right.replaceAll("\\."+X, X+".");
						temp.setLeft(left);
						temp.setRight(newRight);
						if(!pro.contains(temp))
							pro.add(temp);
					}
				}
			}
		}
		core.setProjcetSet(pro);
		go=closure(core);
//		System.out.println("**************������һ��goΪ");
//		for(int i=0;i<go.getProjcetSet().size();i++){
//			System.out.println(go.getProjcetSet().get(i).getLeft()+go.getProjcetSet().get(i).getRight());
//		}
		return go;
		
	}
	public void creatDFA(){           //������Ŀ���淶��
		Grammar Ix;
		Grammar core=new Grammar();
		List<Derivation> projcetSet=new ArrayList<Derivation>();
		Derivation  I0Core= grammar.getProjcetSet().get(0); 
		String newRight="."+I0Core.getRight();
		I0Core.setRight(newRight);     //����I0�ĺ�
		projcetSet.add(I0Core);           
		core.setProjcetSet(projcetSet);
		Ix=closure(core);     //��I0��հ�
		canonical.add(Ix);   //�ŵ���Ŀ����
		//System.out.println("����IO�հ���");
		for(int i=0;i<canonical.size();i++){    //�ƽ�����Ix
			Ix=canonical.get(i);
			//System.out.println("ѭ����ʼ");
			for(int j=0;j<Ix.getGo().size();j++){
				String arcTransfer=Ix.getGo().get(j);
				Grammar temp=go(Ix, arcTransfer);   //�ƽ��Ĺ�����go����
				if(!canonical.contains(temp))
					canonical.add(temp);
			}
			//System.out.println("ѭ������");
		}
		System.out.println(canonical.size());
		for(int i=0;i<canonical.size();i++){
			System.out.println("*****************I"+i);
			for(int j=0;j<canonical.get(i).getProjcetSet().size();j++){
				System.out.println(canonical.get(i).getProjcetSet().get(j).getLeft()+"->"+canonical.get(i).getProjcetSet().get(j).getRight());
				
			}
		}
	}
	public void creatAnalysisTable(){
		for(int i=0;i<canonical.size();i++){
			HashMap<String,String> row=new HashMap<String, String>();   //hashmap�洢action-goto��һ�е�����
			Grammar Ix=canonical.get(i);
			for(int j=0;j<Ix.getGo().size();j++){
				String X=Ix.getGo().get(j);
				Grammar to=go(Ix, X);
				for(int k=0;k<canonical.size();k++){
					if(to.equals(canonical.get(k))){
						if(grammar.getVn().contains(X))
							row.put(X, ""+k);                   //goto���еļ���
						else
							row.put(X,"S"+k);                 //aiction������ƽ���Ŀ
					}
				}
			}
			for(int j=0;j<Ix.getProjcetSet().size();j++){
				String left=Ix.getProjcetSet().get(j).getLeft();
				String right=Ix.getProjcetSet().get(j).getRight();
				if(right.endsWith(".")&&left.equals("S'"))
					row.put("#", "acc");                         //���չ�ԼΪacc��
				else if(right.endsWith(".")){
					for(int k=0;k<grammar.getVt().size();k++){
						String vt=grammar.getVt().get(k);
						if(row.get(vt)!=null){                             //˵�������ƽ�-��Լ���߹�Լ-��Լ��ͻ����lr0�ķ�
							if(grammar.getVn().contains(row.get(vt)))
								System.out.println("���ڹ�Լ��Լ��ͻ");
							else
								System.out.println("�����ƽ���Լ��ͻ");
							for(int z=0;z<grammar.getProjcetSet().size();z++){
								String rightG=right.replaceAll("\\.", "");
								if(left.equals(grammar.getProjcetSet().get(z).getLeft())&&rightG.equals(grammar.getProjcetSet().get(z).getRight())){
									row.put(vt, "r"+z+","+row.get(vt));                       //��Լ��Ŀ����
									row.put("#", "r"+z);
								}
							}
							flag=false;
						}else{
							for(int z=0;z<grammar.getProjcetSet().size();z++){
								String rightG=right.replaceAll("\\.", "");
								if(left.equals(grammar.getProjcetSet().get(z).getLeft())&&rightG.equals(grammar.getProjcetSet().get(z).getRight())){
									row.put(vt, "r"+z);                       //��Լ��Ŀ����
									row.put("#", "r"+z);
								}
							}
						}
					}
				}
			}
		table.add(row);
		}
//		for(int i=0;i<table.size();i++){
//			HashMap<String,String> p=table.get(i);
//			System.out.println("*************��"+i+"��");
//			for(Map.Entry<String, String> m:p.entrySet()){
//				System.out.println(m.getKey()+":"+m.getValue());
//			}
//		}
		for(int i=0;i<grammar.getVt().size();i++){                                //�����￪ʼ��ӡLR(0)������
			System.out.print("\t"+grammar.getVt().get(i));
		}
		System.out.print("\t"+"#");
		for(int i=1;i<grammar.getVn().size();i++){
			System.out.print("\t"+grammar.getVn().get(i));
		}
		System.out.println("");
		for(int i=0;i<table.size();i++){
			System.out.print(i);
			for(int j=0;j<grammar.getVt().size();j++){
				System.out.print("\t"+table.get(i).get(grammar.getVt().get(j)));
			}
			System.out.print("\t"+table.get(i).get("#"));
			for(int j=1;j<grammar.getVn().size();j++){
				System.out.print("\t"+table.get(i).get(grammar.getVn().get(j)));
			}
			System.out.println("");
		}
		
	}
	public void analyze(){
		Stack<Integer> status=new Stack<Integer>();   //״̬ջ
		Stack<String> symbol=new Stack<String>();    //����ջ
		Stack<String> inputString=new Stack<String>(); //������Ŵ�
		status.push(0);   //��ʼ��״̬ջ
		symbol.push("#"); //��ʼ������ջ
		if(!flag){
			System.exit(0);
		}
		Scanner in=new Scanner(System.in);
		System.out.println("����������Ҫ�����ľ���");
		String input=in.next();
		for(int i=input.length()-1;i>=0;i--){
			inputString.push(String.valueOf(input.charAt(i)));
		}
		
		int step=0;
		System.out.printf("%-8s%-16s%-9s%-10s%-11s%-10s","����","״̬ջ","����ջ","���봮","ACTION","GOTO");
		System.out.println("");
		while(!inputString.empty()){
			step++;
			System.out.printf("%-5s",step);
			String outString=inputString.peek();  //�����봮ջ��
			Integer outStatus=status.peek();       //��״̬ջջ��
			String action=table.get(outStatus).get(outString);
			if(action==null){
				System.out.println("�þ��Ӳ�����LR(0)�ķ�");
				break;
			}else if(action.startsWith("S")){           //�ƽ�����
					printIntStack(status);
					printStack(symbol);
					printStringStack(inputString);
					System.out.printf("%10s",action+"\n");
					status.push(Integer.valueOf(String.valueOf(action.charAt(1))));   //״̬��ջ
					symbol.push(inputString.pop());                //���봮�������ջ
				  }else if(action.startsWith("r")){                //��Լ��Ŀ
					  printIntStack(status);
					  printStack(symbol);
					  printStringStack(inputString);
					  System.out.printf("%9s",action);
					  Derivation gra=  grammar.getProjcetSet().get(Integer.valueOf(String.valueOf(action.charAt(1))));
					  String left=gra.getLeft();
					  String right=gra.getRight();
					  for(int i=0;i<right.length();i++){
						  symbol.pop();
						  status.pop();
					  }
					  symbol.push(left);
					  String Goto=table.get(status.peek()).get(left);
					  if(Goto==null){
						  System.out.println("�þ��Ӳ�����LR(0)�ķ�");
					  }else{
						  System.out.printf("%10s",Goto+"\n");
						  status.push(Integer.valueOf(Goto));
					  }
				  }else if(action.startsWith("a")){
					  printIntStack(status);
					  printStack(symbol);
					  printStringStack(inputString);
					  System.out.printf("%10s",action+"\n");
					  System.out.println("�þ��ӷ���LR(0)�ķ�");
					  break;
				  }
			
		}
	}
	private static void printIntStack(Stack<Integer> stack ){
        if (stack.empty())
            System.out.println("��ջ�ǿյģ�û��Ԫ��");
            else {
                //System.out.print("��ջ�е�Ԫ�أ�");
            	String output="";
                Enumeration items = stack.elements(); // �õ� stack �е�ö�ٶ��� 
                while (items.hasMoreElements()) //��ʾö�٣�stack �� �е�����Ԫ��
                {
                	Integer num=(Integer) items.nextElement();
                	if(num>9)
                		output+="("+num+")";
                	else
                		output+=num;
                }
                System.out.printf("%-10s",output);
            }
    }
	private static void printStack(Stack<String> stack ){
	        if (stack.empty())
	            System.out.println("��ջ�ǿյģ�û��Ԫ��");
	            else {
	                //System.out.print("��ջ�е�Ԫ�أ�");
	            	String output="";
	                Enumeration items = stack.elements(); // �õ� stack �е�ö�ٶ��� 
	                while (items.hasMoreElements()) //��ʾö�٣�stack �� �е�����Ԫ��
	                	output+=items.nextElement();
	                System.out.printf("%-5s",output);
	            }
	    }
	private static void printStringStack(Stack<String> stack ){
        if (stack.empty())
            System.out.println("��ջ�ǿյģ�û��Ԫ��");
            else {
                //System.out.print("��ջ�е�Ԫ�أ�");
            	String output="";
                Enumeration items = stack.elements(); // �õ� stack �е�ö�ٶ��� 
                while (items.hasMoreElements()) //��ʾö�٣�stack �� �е�����Ԫ��
                    output=items.nextElement()+output;
                System.out.printf("%5s",output);
            }
    }
	public static void main(String[] args){
		Analysis lr=new Analysis();
		lr.input();
		lr.creatDFA();
		lr.creatAnalysisTable();
		lr.analyze();
	}
}
