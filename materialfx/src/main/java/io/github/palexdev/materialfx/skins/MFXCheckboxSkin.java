package io.github.palexdev.materialfx.skins;

import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.factories.RippleClipTypeFactory;
import io.github.palexdev.materialfx.effects.RippleGenerator;
import io.github.palexdev.materialfx.utils.NodeUtils;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.skin.CheckBoxSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

/**
 *  This is the implementation of the {@code Skin} associated with every {@code MFXCheckbox}.
 */
public class MFXCheckboxSkin extends CheckBoxSkin {
    //================================================================================
    // Properties
    //================================================================================
    private final AnchorPane rippleContainer;
    private final StackPane box;
    private final StackPane mark;
    private final RippleGenerator rippleGenerator;

    private final double rippleContainerSize = 30;
    private final double boxSize = 26;

    private final double labelOffset = 2;

    //================================================================================
    // Constructors
    //================================================================================
    public MFXCheckboxSkin(MFXCheckbox checkbox) {
        super(checkbox);

        // Contains the ripple generator and the box
        rippleContainer = new AnchorPane();
        rippleContainer.setPrefSize(rippleContainerSize, rippleContainerSize);
        rippleContainer.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        rippleContainer.getStyleClass().setAll("ripple-container");

        // To make ripple container appear like a Circle
        NodeUtils.makeRegionCircular(rippleContainer, rippleContainerSize * 0.55);

        // Contains the mark which is a SVG path defined in CSS
        box = new StackPane();
        box.setPrefSize(boxSize, boxSize);
        box.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        box.getStyleClass().setAll("box");
        box.setBorder(new Border(new BorderStroke(
                checkbox.getUncheckedColor(),
                BorderStrokeStyle.SOLID,
                new CornerRadii(2),
                new BorderWidths(2.2)
        )));
        box.setBackground(new Background(new BackgroundFill(
                Color.TRANSPARENT,
                new CornerRadii(2),
                Insets.EMPTY
                )));

        mark = new StackPane();
        mark.getStyleClass().setAll("mark");
        box.getChildren().add(mark);

        rippleGenerator = new RippleGenerator(rippleContainer, new RippleClipTypeFactory());
        rippleGenerator.setRippleRadius(18);
        rippleGenerator.setInDuration(Duration.millis(400));
        rippleGenerator.setAnimateBackground(false);

        rippleContainer.getChildren().addAll(rippleGenerator, box);

        updateChildren();
        updateMarkType();
        setListeners();
    }

    //================================================================================
    // Methods
    //================================================================================

    /**
     * Adds listeners for: markType, selected, indeterminate, checked and unchecked coloros properties.
     */
    private void setListeners() {
        MFXCheckbox checkbox = (MFXCheckbox) getSkinnable();

        checkbox.markTypeProperty().addListener(
                (observable, oldValue, newValue) -> updateMarkType()
        );

        checkbox.selectedProperty().addListener(
                (observable, oldValue, newValue) -> updateColors()
        );

        checkbox.indeterminateProperty().addListener(
                (observable, oldValue, newValue) -> updateColors()
        );

        checkbox.checkedColorProperty().addListener(
                (observable, oldValue, newValue) -> updateColors()
        );

        checkbox.uncheckedColorProperty().addListener(
                (observable, oldValue, newValue) -> updateColors()
        );

        /* Listener on control but if the coordinates of the event are greater than then ripple container size
         * then the center of the ripple is set to the width and/or height of container
         */
        checkbox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            rippleGenerator.setGeneratorCenterX(Math.min(event.getX(), rippleContainer.getWidth()));
            rippleGenerator.setGeneratorCenterY(Math.min(event.getY(), rippleContainer.getHeight()));
            rippleGenerator.createRipple();
        });

        /*
         * Workaround
         * When the control is created the Skin is still null, so if the CheckBox is set
         * to be selected/indeterminate the animation won't be played. To fix this add a listener to the
         * control's skinProperty, when the skin is not null and the CheckBox isSelected/isIndeterminate,
         * play the animation.
         */
        checkbox.skinProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && (checkbox.isSelected() || checkbox.isIndeterminate())) {
                updateColors();
            }
        });
    }

    /**
     * This method is called whenever one of the following properties changes:
     * {@code selectedProperty}, {@code indeterminateProperty}, {@code checkedColor} and {@code uncheckedColor} properties
     * @see NodeUtils
     */
    private void updateColors() {
        MFXCheckbox checkbox = (MFXCheckbox) getSkinnable();

        final BorderStroke borderStroke = box.getBorder().getStrokes().get(0);
        if (checkbox.isIndeterminate()) {
            NodeUtils.updateBackground(box, checkbox.getCheckedColor(), new Insets(4));
        } else if (checkbox.isSelected()) {
            NodeUtils.updateBackground(box, checkbox.getCheckedColor(), Insets.EMPTY);
            box.setBorder(new Border(new BorderStroke(
                    checkbox.getCheckedColor(),
                    borderStroke.getTopStyle(),
                    borderStroke.getRadii(),
                    borderStroke.getWidths()
            )));
        } else {
            NodeUtils.updateBackground(box, Color.TRANSPARENT);
            box.setBorder(new Border(new BorderStroke(
                    checkbox.getUncheckedColor(),
                    borderStroke.getTopStyle(),
                    borderStroke.getRadii(),
                    borderStroke.getWidths()
            )));
        }
    }

    /**
     * This method is called whenever the {@code markType} property changes.
     */
    private void updateMarkType() {
        MFXCheckbox checkbox = (MFXCheckbox) getSkinnable();

        SVGPath svgPath = new SVGPath();
        svgPath.setContent(checkbox.getMarkType().getSvhPath());
        mark.setShape(svgPath);
    }

    /**
     * Centers the box in the ripple container
     */
    private void centerBox() {
        final double offsetPercentage = 3;
        final double vInset = ((rippleContainerSize - boxSize) / 2) * offsetPercentage;
        final double hInset = ((rippleContainerSize - boxSize) / 2) * offsetPercentage;
        AnchorPane.setTopAnchor(box, vInset);
        AnchorPane.setRightAnchor(box, hInset);
        AnchorPane.setBottomAnchor(box, vInset);
        AnchorPane.setLeftAnchor(box, hInset);
    }

    //================================================================================
    // Override Methods
    //================================================================================
    @Override
    protected void updateChildren() {
        super.updateChildren();
        if (rippleContainer != null) {
            getChildren().remove(1);
            getChildren().add(rippleContainer);
        }
    }

    @Override
    protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset) +
                snapSizeX(rippleContainer.minWidth(-1));
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) +
                snapSizeX(rippleContainer.prefWidth(-1));
    }

    @Override
    protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(super.computeMinHeight(width - rippleContainer.minWidth(-1), topInset, rightInset, bottomInset, leftInset),
                topInset + rippleContainer.minHeight(-1) + bottomInset) + topInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return Math.max(super.computePrefHeight(width - rippleContainer.prefWidth(-1), topInset, rightInset, bottomInset, leftInset),
                topInset + rippleContainer.prefHeight(-1) + bottomInset);
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        final CheckBox checkBox = getSkinnable();

        final double boxWidth = snapSizeX(rippleContainer.prefWidth(-1));
        final double boxHeight = snapSizeY(rippleContainer.prefHeight(-1));
        final double computeWidth = Math.max(checkBox.prefWidth(-1), checkBox.minWidth(-1));
        final double labelWidth = Math.min( computeWidth - boxWidth, w - snapSizeX(boxWidth));
        final double labelHeight = Math.min(checkBox.prefHeight(labelWidth), h);
        final double maxHeight = Math.max(boxHeight, labelHeight);
        final double xOffset = NodeUtils.computeXOffset(w, labelWidth + boxWidth, checkBox.getAlignment().getHpos()) + x;
        final double yOffset = NodeUtils.computeYOffset(h, maxHeight, checkBox.getAlignment().getVpos()) + y;

        layoutLabelInArea(xOffset + boxWidth + labelOffset, yOffset, labelWidth, maxHeight, checkBox.getAlignment());
        rippleContainer.resize(boxWidth, boxHeight);
        positionInArea(rippleContainer, xOffset, yOffset, boxWidth, maxHeight, 0, checkBox.getAlignment().getHpos(), checkBox.getAlignment().getVpos());

        centerBox();
    }
}


