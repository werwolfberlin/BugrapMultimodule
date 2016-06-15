package com.vaadin.gerald;

import com.vaadin.gerald.listeners.ValueChangedSupport;
import com.vaadin.ui.Button;

import java.io.Serializable;
import java.util.*;

public class ButtonGroup extends ValueChangedSupport<ButtonGroup, Button> implements Serializable
{
  private static final String             PUSHED_STYLE = "option-button-selected";

  private final List<Button> buttons = new ArrayList<>();
  private Button selectedButton = null;

  public ButtonGroup()
  {
  }

  public ButtonGroup(Button ... buttons)
  {
    if(buttons.length == 0)
    {
      return;
    }

    for (Button button : buttons)
    {
      this.buttons.add(button);
      button.addStyleName("option-button");
      button.addClickListener((Button.ClickListener) this::updateButtonSelection);
    }

    this.buttons.get(0).addStyleName("rounded-corners-left");
    this.buttons.get(this.buttons.size()-1).addStyleName("rounded-corners-right");
  }

  private void updateButtonSelection(Button.ClickEvent event)
  {
    if(selectedButton == event.getButton()) return;

    for (Button button : buttons)
    {
      button.removeStyleName(PUSHED_STYLE);
    }

    Button oldSelectedButton = selectedButton;
    selectedButton = event.getButton();
    selectedButton.addStyleName(PUSHED_STYLE);

    fireValueChanged(this, oldSelectedButton, selectedButton);
  }

  public Button getSelectedButton()
  {
    return selectedButton;
  }

  public void setSelectedButton(Button selectedButton)
  {
    updateButtonSelection(new Button.ClickEvent(selectedButton));
  }

  public Collection<Button> getButtons() {return Collections.unmodifiableCollection(buttons);}
}
