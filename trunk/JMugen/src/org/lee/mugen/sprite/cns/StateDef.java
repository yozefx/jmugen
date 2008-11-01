package org.lee.mugen.sprite.cns;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.lee.mugen.core.StateMachine;
import org.lee.mugen.parser.type.Functionable;
import org.lee.mugen.sprite.character.Sprite;
import org.lee.mugen.sprite.parser.CnsParse;
import org.lee.mugen.sprite.parser.Parser.GroupText;

public class StateDef implements Cloneable {
	
	private String id;
	private List<StateCtrl> stateCtrlList = new ArrayList<StateCtrl>();
	private List<Functionable> executors = new ArrayList<Functionable>();
	
	private boolean isCompiled = false;
	private List<GroupText> groups = new LinkedList<GroupText>();
	
	public void addGroup(GroupText grp) {
		groups.add(grp);
	}
	
	
	public List<GroupText> getGroups() {
		return groups;
	}


	@Override
	public String toString() {
		return "StateDef N° " + getId();
	}
	
	public StateDef(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	
	public void addStateCtrl(StateCtrl stateCtrl) {
		stateCtrlList.add(stateCtrl);
	}
	public void addExecutor(Functionable f) {
		executors.add(f);
	}
	private boolean isExecMainFunc = false;
	public void execute(String spriteId) {
		if (!isCompiled) {
			Sprite sprite = StateMachine.getInstance().getSpriteInstance(spriteId);
			try {
				getStateCtrls().clear();
				executors.clear();
				if (getIntId() < 0) {
					CnsParse.buildSpriteInfoForReal(groups, sprite, sprite.getInfo(), sprite.getSpriteState());
					
				} else 
					CnsParse.buildSpriteInfoForReal(groups, sprite, sprite.getInfo(), sprite.getSpriteState());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			isCompiled = true;
		}
		if (StateMachine.getInstance().getSpriteInstance(spriteId) == null)
			return; // case where helper is not add again
		long time = StateMachine.getInstance().getSpriteInstance(spriteId).getSpriteState().getTimeInState();
		
		if (!isExecMainFunc) {
			isExecMainFunc = true;
			for (Functionable f: executors) {
				try {
					f.getValue(spriteId);
					
				} catch (Exception e) {
					System.err.println("Unkown Setter : " + f.toString());
				}
			}
		}
		int pos = 0;
		for (StateCtrl state: stateCtrlList) {
			try {
				if (state.execute(spriteId)) {
					if (state.isHasInterrupFunction()) {
						break;
					}
				}
				pos++;
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("statedef Error : " + id + " - sctrl :  " + state.getId());
			}
		}
	}
	public void reset(String spriteId) {
//		stateTime = 0;
		isExecMainFunc = false;
		for (StateCtrl state: stateCtrlList) {
			state.reset();
		}
		
	}
	public List<StateCtrl> getStateCtrls() {
		return stateCtrlList;
	}

	public int getIntId() {
		return Integer.parseInt(id);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		StateDef stateDef = new StateDef(id);
		
		stateDef.stateCtrlList = (List<StateCtrl>) ((ArrayList)stateCtrlList).clone();
		stateDef.executors = executors; // Not need to clone
		
		return stateDef;
	}

	public void setStateCtrlList(List<StateCtrl> stateCtrlList) {
		this.stateCtrlList = stateCtrlList;
	}


	public void recompile() {
		isCompiled = false;
		
	}


	public void setCompiled() {
		isCompiled = true;
	}

//	public int getStateTime() {
//		return stateTime;
//	}
	
	
}
