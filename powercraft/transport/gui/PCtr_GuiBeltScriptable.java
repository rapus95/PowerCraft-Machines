package powercraft.transport.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.tools.Diagnostic;

import org.lwjgl.input.Keyboard;

import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.PC_GresButton;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresGuiHandler;
import powercraft.api.gres.PC_GresMultilineHighlightingTextEdit;
import powercraft.api.gres.PC_GresWindow;
import powercraft.api.gres.PC_GresWindowSideTab;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.autoadd.PC_AutoAdd;
import powercraft.api.gres.autoadd.PC_AutoComplete;
import powercraft.api.gres.autoadd.PC_StringWithInfo;
import powercraft.api.gres.doc.PC_GresHighlighting;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent.Event;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.font.PC_FontTexture;
import powercraft.api.gres.font.PC_Fonts;
import powercraft.api.gres.layout.PC_GresLayoutHorizontal;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.script.miniscript.PC_MiniscriptHighlighting;
import powercraft.transport.tileentity.PCtr_TileEntityBeltScriptable;

public class PCtr_GuiBeltScriptable implements PC_IGresGui, PC_IGresEventListener {

	private PCtr_TileEntityBeltScriptable te;
	private String source;
	private PC_GresMultilineHighlightingTextEdit textEdit;
	private PC_GresButton save;
	private PC_GresButton cancel;
	private List<Diagnostic<?>> diagnostics;
	
	public PCtr_GuiBeltScriptable(PCtr_TileEntityBeltScriptable te, String source, List<Diagnostic<?>> diagnostics) {
		this.te = te;
		this.source = source;
		this.diagnostics = diagnostics;
	}

	private static void addTo(HashMap<String, PC_StringWithInfo> map, HashMap<String, Integer> other){
		for(Entry<String, Integer> e:other.entrySet()){
			String key = e.getKey();
			String[] splittet = key.split("\\.");
			String path = null;
			for(int i=0; i<splittet.length-1; i++){
				if(path==null){
					path = splittet[i];
				}else{
					path += splittet[i];
				}
				if(!map.containsKey(path)){
					map.put(path, new PC_StringWithInfo(splittet[i], "Package: "+path));
				}
			}
			map.put(key, new PC_StringWithInfo(splittet[splittet.length-1], "Const: "+e.getValue()));
		}
	}
	
	@Override
	public void initGui(PC_GresGuiHandler gui) {
		PC_FontTexture fontTexture = PC_Fonts.getFontByName("Consolas", 24, 0);
		PC_GresHighlighting highlighting = PC_MiniscriptHighlighting.makeHighlighting(this.te.getConsts().keySet(), this.te.getPointers().keySet());
		PC_AutoAdd autoAdd = PC_MiniscriptHighlighting.makeAutoAdd();
		HashMap<String, PC_StringWithInfo> list = new HashMap<String, PC_StringWithInfo>();
		addTo(list, this.te.getConsts());
		
		HashMap<String, PC_StringWithInfo> list2 = new HashMap<String, PC_StringWithInfo>();
		addTo(list2, this.te.getPointers());
		
		PC_AutoComplete autoComplete = PC_MiniscriptHighlighting.makeAutoComplete(list, list2);
		PC_GresWindow win = new PC_GresWindow("Belt");
		win.addSideTab(PC_GresWindowSideTab.createRedstoneSideTab(this.te));
		win.setLayout(new PC_GresLayoutVertical());
		this.textEdit = new PC_GresMultilineHighlightingTextEdit(fontTexture, highlighting, autoAdd, autoComplete, this.source);
		if(this.diagnostics!=null){
			this.textEdit.setErrors(this.diagnostics);
		}
		win.add(this.textEdit);
		PC_GresGroupContainer gc = new PC_GresGroupContainer();
		gc.setFill(Fill.HORIZONTAL);
		gc.setLayout(new PC_GresLayoutHorizontal());
		this.save = new PC_GresButton("Save & Compile");
		this.save.addEventListener(this);
		gc.add(this.save);
		this.cancel = new PC_GresButton("Cancel");
		this.cancel.addEventListener(this);
		gc.add(this.cancel);
		win.add(gc);
		gui.add(win);
		gui.addEventListener(this);
	}

	@Override
	public void onEvent(PC_GresEvent event) {
		PC_GresComponent component = event.getComponent();
		if(event instanceof PC_GresKeyEvent){
			PC_GresKeyEvent kEvent = (PC_GresKeyEvent)event;
			if(kEvent.getKeyCode()==Keyboard.KEY_ESCAPE){
				component.getGuiHandler().close();
			}
		}else if(event instanceof PC_GresMouseButtonEvent){
			PC_GresMouseButtonEvent mbe = (PC_GresMouseButtonEvent)event;
			if(mbe.getEvent()==Event.CLICK){
				if(mbe.getComponent()==this.cancel){
					component.getGuiHandler().close();
				}else if(mbe.getComponent()==this.save){
					this.te.sendSaveMessage(this.textEdit.getText());
					this.textEdit.removeErrors();
					this.diagnostics = null;
				}
			}
		}
	}

	public void setErrors(List<Diagnostic<?>> diagnostics) {
		this.diagnostics = diagnostics;
		this.textEdit.setErrors(diagnostics);
	}
	
}
