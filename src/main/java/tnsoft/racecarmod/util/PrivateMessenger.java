package tnsoft.racecarmod.util;

import java.util.Iterator;
import java.util.List;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

public class PrivateMessenger implements ITextComponent {

	private String message;
	
	public PrivateMessenger(String message)
	{
		this.message = message;
	}
	
	@Override
	public Iterator<ITextComponent> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITextComponent setStyle(Style style) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Style getStyle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITextComponent appendText(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITextComponent appendSibling(ITextComponent component) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUnformattedComponentText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUnformattedText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFormattedText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ITextComponent> getSiblings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITextComponent createCopy() {
		// TODO Auto-generated method stub
		return null;
	}

}
