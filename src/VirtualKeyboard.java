import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.PolygonBuilder;

public class VirtualKeyboard {
  private final VBox root ;
  
  /**
   * Creates a Virtual Keyboard. 
   * @param target The node that will receive KeyEvents from this keyboard.
   */
  public VirtualKeyboard(ReadOnlyObjectProperty<Node> target) {
    this.root = new VBox(5);
    root.setPadding(new Insets(10));
    root.getStyleClass().add("virtual-keyboard");

    final Modifiers modifiers = new Modifiers();

    // Data for regular buttons; split into rows
    final String[][] unshifted = new String[][] {
        { "`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=" },
        { "q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "[", "]", "\\" },
        { "a", "s", "d", "f", "g", "h", "j", "k", "l", ";", "'" },
        { "z", "x", "c", "v", "b", "n", "m", ",", ".", "/" } };

    final String[][] shifted = new String[][] {
        { "~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+" },
        { "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "{", "}", "|" },
        { "A", "S", "D", "F", "G", "H", "J", "K", "L", ":", "\"" },
        { "Z", "X", "C", "V", "B", "N", "M", "<", ">", "?" } };

    final KeyCode[][] codes = new KeyCode[][] {
        { KeyCode.BACK_QUOTE, KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3,
            KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7,
            KeyCode.DIGIT8, KeyCode.DIGIT9, KeyCode.DIGIT0, KeyCode.SUBTRACT,
            KeyCode.EQUALS },
        { KeyCode.Q, KeyCode.W, KeyCode.E, KeyCode.R, KeyCode.T, KeyCode.Y,
            KeyCode.U, KeyCode.I, KeyCode.O, KeyCode.P, KeyCode.OPEN_BRACKET,
            KeyCode.CLOSE_BRACKET, KeyCode.BACK_SLASH },
        { KeyCode.A, KeyCode.S, KeyCode.D, KeyCode.F, KeyCode.G, KeyCode.H,
            KeyCode.J, KeyCode.K, KeyCode.L, KeyCode.SEMICOLON, KeyCode.QUOTE },
        { KeyCode.Z, KeyCode.X, KeyCode.C, KeyCode.V, KeyCode.B, KeyCode.N,
            KeyCode.M, KeyCode.COMMA, KeyCode.PERIOD, KeyCode.SLASH } };

    // non-regular buttons (don't respond to Shift)
    final Button escape = createButtonWithFixedText("Esc", KeyCode.ESCAPE, modifiers, target);
    final Button backspace = createButtonWithFixedText("Backspace", KeyCode.BACK_SPACE, modifiers, target);
    final Button delete = createButtonWithFixedText("Del", KeyCode.DELETE, modifiers, target);
    final Button enter = createButtonWithFixedText("Enter", KeyCode.ENTER,  modifiers, target);
    final Button tab = createButtonWithFixedText("Tab", KeyCode.TAB, modifiers, target);

    // Cursor keys, with graphic instead of text
    final Button cursorLeft = createButtonWithFixedText("", KeyCode.LEFT, modifiers, target);
    cursorLeft.setGraphic(PolygonBuilder.create()
        .points(15.0, 5.0, 15.0, 15.0, 5.0, 10.0).fill(Color.rgb(64, 64, 64))
        .build());
    cursorLeft.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

    final Button cursorRight = createButtonWithFixedText("", KeyCode.RIGHT, modifiers, target);
    cursorRight.setGraphic(PolygonBuilder.create()
        .points(5.0, 5.0, 5.0, 15.0, 15.0, 10.0).fill(Color.rgb(64, 64, 64))
        .build());
    cursorRight.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    
    final Button cursorUp = createButtonWithFixedText("", KeyCode.UP, modifiers, target);
    cursorUp.setGraphic(PolygonBuilder.create()
        .points(10.0, 0.0, 15.0, 5.0, 5.0, 5.0).fill(Color.rgb(64,  64,  64))
        .build());
    cursorUp.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    
    final Button cursorDown = createButtonWithFixedText("", KeyCode.DOWN, modifiers, target);
    cursorDown.setGraphic(PolygonBuilder.create()
        .points(10.0, 5.0, 15.0, 0.0, 5.0, 0.0).fill(Color.rgb(64,  64,  64))
        .build());
    cursorDown.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    
    final VBox cursorUpDown = new VBox(2);
    cursorUpDown.getChildren().addAll(cursorUp, cursorDown);

    // "Extras" to go at the left or right end of each row of buttons.
    final Node[][] extraLeftButtons = new Node[][] { {escape}, {tab}, {modifiers.capsLockKey()}, {modifiers.shiftKey()} };
    final Node[][] extraRightButtons = new Node[][] { {backspace}, {delete}, {enter}, {modifiers.secondShiftKey()} };

    // build layout
    for (int row = 0; row < unshifted.length; row++) {
      HBox hbox = new HBox(5);
      hbox.setAlignment(Pos.CENTER);
      root.getChildren().add(hbox);

      if (extraLeftButtons[row] != null) {
        hbox.getChildren().addAll(extraLeftButtons[row]);
      }

      for (int k = 0; k < unshifted[row].length; k++) {
        hbox.getChildren().add( createButton(unshifted[row][k], shifted[row][k], codes[row][k], modifiers, target));
      }

      if (extraRightButtons[row] != null) {
        hbox.getChildren().addAll(extraRightButtons[row]);
      }

    }

    final Button spaceBar = createButton(" ", " ", KeyCode.SPACE, modifiers, target);
    spaceBar.setMaxWidth(Double.POSITIVE_INFINITY);
    HBox.setHgrow(spaceBar, Priority.ALWAYS);

    final HBox bottomRow = new HBox(5);
    bottomRow.setAlignment(Pos.CENTER);
    bottomRow.getChildren().addAll(modifiers.ctrlKey(), modifiers.altKey(),
        modifiers.metaKey(), spaceBar, cursorLeft, cursorUpDown, cursorRight);
    root.getChildren().add(bottomRow);    
  }
  
  /**
   * Visual component displaying this keyboard
   * @return A view of the keyboard
   */
  public Node view() {
    return root ;
  }
  
  // Creates a "regular" button that has an unshifted and shifted value
  private Button createButton(final String unshifted, final String shifted,
      final KeyCode code, Modifiers modifiers, final ReadOnlyObjectProperty<Node> target) {
    final ReadOnlyBooleanProperty letter = new SimpleBooleanProperty( unshifted.length() == 1 && Character.isLetter(unshifted.charAt(0)));
    final StringBinding text = 
        Bindings.when(modifiers.shiftDown().or(modifiers.capsLockOn().and(letter)))
        .then(shifted)
        .otherwise(unshifted);
    Button button = createButton(text, code, modifiers, target);
    button.textProperty().bind(text);
    return button;
  }

  // Creates a button with fixed text not responding to Shift
  private Button createButtonWithFixedText(final String text, final KeyCode code, final Modifiers modifiers, final ReadOnlyObjectProperty<Node> target) {
    StringProperty textProperty = new SimpleStringProperty(text);
    Button button = createButton(textProperty, code, modifiers, target);
    button.setText(text);
    return button;
  }
  
  // Creates a button with mutable text, and registers listener with it
  private Button createButton(final ObservableStringValue text, final KeyCode code, final Modifiers modifiers, final ReadOnlyObjectProperty<Node> target) {
    final Button button = new Button();
    
    // Important not to grab the focus from the target:
    button.setFocusTraversable(false);
    
    // Add a style class for css:
    button.getStyleClass().add("virtual-keyboard-button");
    
    button.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        final Node targetNode = target.get();
        if (targetNode != null) {
          final String character;
          if (text.get().length() == 1) {
            character = text.get();
          } else {
            character = KeyEvent.CHAR_UNDEFINED;
          }
          final KeyEvent keyPressEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_PRESSED, character, code,
              modifiers);
          targetNode.fireEvent(keyPressEvent);
          final KeyEvent keyReleasedEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_RELEASED, character,
              code, modifiers);
          targetNode.fireEvent(keyReleasedEvent);
          if (character != KeyEvent.CHAR_UNDEFINED) {
            final KeyEvent keyTypedEvent = createKeyEvent(button, targetNode, KeyEvent.KEY_TYPED, character, code,
                modifiers);
            targetNode.fireEvent(keyTypedEvent);
          }
          modifiers.releaseKeys();
        }
      }
    });
    return button;
  }

  // Utility method to create a KeyEvent from the Modifiers
  private KeyEvent createKeyEvent(Object source, EventTarget target,
      EventType<KeyEvent> eventType, String character, KeyCode code,
      Modifiers modifiers) {
    return new KeyEvent(source, target, eventType, character, code.toString(),
        code, modifiers.shiftDown().get(), modifiers.ctrlDown().get(),
        modifiers.altDown().get(), modifiers.metaDown().get());
  }
  
  // Convenience class to bundle together the modifier keys and their selected state
  private static class Modifiers {
    private final ToggleButton shift;
    private final ToggleButton shift2;
    private final ToggleButton ctrl;
    private final ToggleButton alt;
    private final ToggleButton meta;
    private final ToggleButton capsLock;

    Modifiers() {
      this.shift = createToggle("Shift");
      this.shift2 = createToggle("Shift");
      this.ctrl = createToggle("Ctrl");
      this.alt = createToggle("Alt");
      this.meta = createToggle("Meta");
      this.capsLock = createToggle("Caps");

      shift2.selectedProperty().bindBidirectional(shift.selectedProperty());
    }

    private ToggleButton createToggle(final String text) {
      final ToggleButton tb = new ToggleButton(text);
      tb.setFocusTraversable(false);
      return tb;
    }

    public ToggleButton shiftKey() {
      return shift;
    }

    public ToggleButton secondShiftKey() {
      return shift2;
    }

    public ToggleButton ctrlKey() {
      return ctrl;
    }

    public ToggleButton altKey() {
      return alt;
    }

    public ToggleButton metaKey() {
      return meta;
    }

    public ToggleButton capsLockKey() {
      return capsLock;
    }

    public BooleanProperty shiftDown() {
      return shift.selectedProperty();
    }

    public BooleanProperty ctrlDown() {
      return ctrl.selectedProperty();
    }

    public BooleanProperty altDown() {
      return alt.selectedProperty();
    }

    public BooleanProperty metaDown() {
      return meta.selectedProperty();
    }

    public BooleanProperty capsLockOn() {
      return capsLock.selectedProperty();
    }

    public void releaseKeys() {
      shift.setSelected(false);
      ctrl.setSelected(false);
      alt.setSelected(false);
      meta.setSelected(false);
    }
  }  
}
