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
	Grammar grammar=new Grammar();  //输入的文法
	List<Grammar> canonical=new ArrayList<Grammar>();  //项目集规范族
	List<HashMap<String,String>> table=new ArrayList<HashMap<String,String>>();//分析表 
	public  void input(){
		Integer n=0;
		Scanner in=new Scanner(System.in);
		System.out.println("请输入文法规则个数");
		n=in.nextInt();
		System.out.println("请输入文法");
		List<Derivation> projcetSet=new ArrayList<Derivation>(); //放文法规则集
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
			if(!projcetSet.contains(der))   //如果规则集不包括这个文法
				projcetSet.add(der);
			if(!vn.contains(gra[0]))        //左部都是非终结符，如果非终结符集不包括这个非终结符
				vn.add(gra[0]);
			
		}
		
	
		grammar.setProjcetSet(projcetSet);
		grammar.setVn(vn);
		for(int i=0;i<grammar.getProjcetSet().size();i++){
			String rightTemp=grammar.getProjcetSet().get(i).getRight();
			for(int j=0;j<rightTemp.length();j++){   //从右部提取终结符
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
	public Grammar closure(Grammar core){                      //求核的闭包
		Grammar gra=new Grammar();
		List<Derivation> projcetSet=new ArrayList<Derivation>();
		List<String> go=new ArrayList<String>();
		for(int i=0;i<core.getProjcetSet().size();i++){        //先把核填到闭包里
			projcetSet.add(core.getProjcetSet().get(i));
		}
		for(int i=0;i<projcetSet.size();i++){       //寻找.后面是非终结符的项目
			String right=projcetSet.get(i).getRight();
			for(int j=0;j<right.length();j++){  
				if(String.valueOf(right.charAt(j)).equals(".")){//如果找到点
					if((j+1)<right.length()){                    //把点后面的符号放到可移进符号集，另外需要去重
						if(!go.contains(String.valueOf(right.charAt(j+1))))
							go.add(String.valueOf(right.charAt(j+1)));
					}
					if((j+1)<right.length()&&grammar.getVn().contains(String.valueOf(right.charAt(j+1)))){  //而且还是非终结符
						for(int k=0;k<grammar.getProjcetSet().size();k++){  //遍历文法规则
							if(grammar.getProjcetSet().get(k).getLeft().equals(String.valueOf(right.charAt(j+1)))){ //找到对应的文法
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
	public Grammar go(Grammar I,String X){                     //Go函数 GO(I,X)=CLOSURE(J)
		
		Grammar go;
		Grammar core=new Grammar();
		List<Derivation> pro =new ArrayList<Derivation>();
		for(int i=0;i<I.getProjcetSet().size();i++){
			String left=I.getProjcetSet().get(i).getLeft();
			String right=I.getProjcetSet().get(i).getRight();
			for(int j=0;j<right.length();j++){
				if(String.valueOf(right.charAt(j)).equals(".")){//如果找到点
					if((j+1)<right.length()&&String.valueOf(right.charAt(j+1)).equals(X)){  //.后等于要移进的X
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
//		System.out.println("**************新生成一个go为");
//		for(int i=0;i<go.getProjcetSet().size();i++){
//			System.out.println(go.getProjcetSet().get(i).getLeft()+go.getProjcetSet().get(i).getRight());
//		}
		return go;
		
	}
	public void creatDFA(){           //构造项目集规范族
		Grammar Ix;
		Grammar core=new Grammar();
		List<Derivation> projcetSet=new ArrayList<Derivation>();
		Derivation  I0Core= grammar.getProjcetSet().get(0); 
		String newRight="."+I0Core.getRight();
		I0Core.setRight(newRight);     //生成I0的核
		projcetSet.add(I0Core);           
		core.setProjcetSet(projcetSet);
		Ix=closure(core);     //对I0求闭包
		canonical.add(Ix);   //放到项目集中
		//System.out.println("求完IO闭包了");
		for(int i=0;i<canonical.size();i++){    //移进生成Ix
			Ix=canonical.get(i);
			//System.out.println("循环开始");
			for(int j=0;j<Ix.getGo().size();j++){
				String arcTransfer=Ix.getGo().get(j);
				Grammar temp=go(Ix, arcTransfer);   //移进的过程是go函数
				if(!canonical.contains(temp))
					canonical.add(temp);
			}
			//System.out.println("循环结束");
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
			HashMap<String,String> row=new HashMap<String, String>();   //hashmap存储action-goto表一行的数据
			Grammar Ix=canonical.get(i);
			for(int j=0;j<Ix.getGo().size();j++){
				String X=Ix.getGo().get(j);
				Grammar to=go(Ix, X);
				for(int k=0;k<canonical.size();k++){
					if(to.equals(canonical.get(k))){
						if(grammar.getVn().contains(X))
							row.put(X, ""+k);                   //goto表中的加入
						else
							row.put(X,"S"+k);                 //aiction表加入移进项目
					}
				}
			}
			for(int j=0;j<Ix.getProjcetSet().size();j++){
				String left=Ix.getProjcetSet().get(j).getLeft();
				String right=Ix.getProjcetSet().get(j).getRight();
				if(right.endsWith(".")&&left.equals("S'"))
					row.put("#", "acc");                         //最终归约为acc的
				else if(right.endsWith(".")){
					for(int k=0;k<grammar.getVt().size();k++){
						String vt=grammar.getVt().get(k);
						if(row.get(vt)!=null){                             //说明存在移进-归约或者归约-归约冲突不是lr0文法
							if(grammar.getVn().contains(row.get(vt)))
								System.out.println("存在归约归约冲突");
							else
								System.out.println("存在移进归约冲突");
							for(int z=0;z<grammar.getProjcetSet().size();z++){
								String rightG=right.replaceAll("\\.", "");
								if(left.equals(grammar.getProjcetSet().get(z).getLeft())&&rightG.equals(grammar.getProjcetSet().get(z).getRight())){
									row.put(vt, "r"+z+","+row.get(vt));                       //归约项目加入
									row.put("#", "r"+z);
								}
							}
							flag=false;
						}else{
							for(int z=0;z<grammar.getProjcetSet().size();z++){
								String rightG=right.replaceAll("\\.", "");
								if(left.equals(grammar.getProjcetSet().get(z).getLeft())&&rightG.equals(grammar.getProjcetSet().get(z).getRight())){
									row.put(vt, "r"+z);                       //归约项目加入
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
//			System.out.println("*************第"+i+"行");
//			for(Map.Entry<String, String> m:p.entrySet()){
//				System.out.println(m.getKey()+":"+m.getValue());
//			}
//		}
		for(int i=0;i<grammar.getVt().size();i++){                                //从这里开始打印LR(0)分析表
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
		Stack<Integer> status=new Stack<Integer>();   //状态栈
		Stack<String> symbol=new Stack<String>();    //符号栈
		Stack<String> inputString=new Stack<String>(); //输入符号串
		status.push(0);   //初始化状态栈
		symbol.push("#"); //初始化符号栈
		if(!flag){
			System.exit(0);
		}
		Scanner in=new Scanner(System.in);
		System.out.println("请输入您想要分析的句子");
		String input=in.next();
		for(int i=input.length()-1;i>=0;i--){
			inputString.push(String.valueOf(input.charAt(i)));
		}
		
		int step=0;
		System.out.printf("%-8s%-16s%-9s%-10s%-11s%-10s","步骤","状态栈","符号栈","输入串","ACTION","GOTO");
		System.out.println("");
		while(!inputString.empty()){
			step++;
			System.out.printf("%-5s",step);
			String outString=inputString.peek();  //读输入串栈顶
			Integer outStatus=status.peek();       //读状态栈栈顶
			String action=table.get(outStatus).get(outString);
			if(action==null){
				System.out.println("该句子不符合LR(0)文法");
				break;
			}else if(action.startsWith("S")){           //移进动作
					printIntStack(status);
					printStack(symbol);
					printStringStack(inputString);
					System.out.printf("%10s",action+"\n");
					status.push(Integer.valueOf(String.valueOf(action.charAt(1))));   //状态入栈
					symbol.push(inputString.pop());                //输入串出入符号栈
				  }else if(action.startsWith("r")){                //归约项目
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
						  System.out.println("该句子不符合LR(0)文法");
					  }else{
						  System.out.printf("%10s",Goto+"\n");
						  status.push(Integer.valueOf(Goto));
					  }
				  }else if(action.startsWith("a")){
					  printIntStack(status);
					  printStack(symbol);
					  printStringStack(inputString);
					  System.out.printf("%10s",action+"\n");
					  System.out.println("该句子符合LR(0)文法");
					  break;
				  }
			
		}
	}
	private static void printIntStack(Stack<Integer> stack ){
        if (stack.empty())
            System.out.println("堆栈是空的，没有元素");
            else {
                //System.out.print("堆栈中的元素：");
            	String output="";
                Enumeration items = stack.elements(); // 得到 stack 中的枚举对象 
                while (items.hasMoreElements()) //显示枚举（stack ） 中的所有元素
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
	            System.out.println("堆栈是空的，没有元素");
	            else {
	                //System.out.print("堆栈中的元素：");
	            	String output="";
	                Enumeration items = stack.elements(); // 得到 stack 中的枚举对象 
	                while (items.hasMoreElements()) //显示枚举（stack ） 中的所有元素
	                	output+=items.nextElement();
	                System.out.printf("%-5s",output);
	            }
	    }
	private static void printStringStack(Stack<String> stack ){
        if (stack.empty())
            System.out.println("堆栈是空的，没有元素");
            else {
                //System.out.print("堆栈中的元素：");
            	String output="";
                Enumeration items = stack.elements(); // 得到 stack 中的枚举对象 
                while (items.hasMoreElements()) //显示枚举（stack ） 中的所有元素
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
