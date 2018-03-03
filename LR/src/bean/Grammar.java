package bean;

import java.util.List;

public class Grammar {
	List<Derivation> projcetSet;//项目集或者文法规则集
	List<String> vn;   //非终结集
	List<String> vt;  //终结符集
	String startTer;  //开始符号
	List<String> go;   //移进符号集
	public List<String> getGo() {
		return go;
	}
	public void setGo(List<String> go) {
		this.go = go;
	}
	public List<Derivation> getProjcetSet() {
		return projcetSet;
	}
	public void setProjcetSet(List<Derivation> projcetSet) {
		this.projcetSet = projcetSet;
	}
	public List<String> getVn() {
		return vn;
	}
	public void setVn(List<String> vn) {
		this.vn = vn;
	}
	public List<String> getVt() {
		return vt;
	}
	public void setVt(List<String> vt) {
		this.vt = vt;
	}
	public String getStartTer() {
		return startTer;
	}
	public void setStartTer(String startTer) {
		this.startTer = startTer;
	}
	@Override
	public boolean equals(Object otherObject){
		if(this == otherObject) return true;
		if(otherObject ==null) return false;
		if(getClass()!=otherObject.getClass())
			return false;
		Grammar other=(Grammar) otherObject;
		Integer num=0;
		if(projcetSet.size()!=other.getProjcetSet().size())
			return false;
		for(int i=0;i<projcetSet.size();i++){
			for(int j=0;j<other.getProjcetSet().size();j++){
				if(projcetSet.get(i).equals(other.getProjcetSet().get(j)))
					num++;
			}
		}
		if(num==projcetSet.size())
			return true;
		return false;
	}
}
