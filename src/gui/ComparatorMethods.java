package gui;

import org.constrains.Constrains;
import org.constrains.View;
import org.constrains.Weight;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ComparatorMethods extends JPanel {
    private final ArrayList<SortingMethod> methods;

    {
        int[] data = {1, 2, 3};
        methods = new ArrayList<>();
        methods.add(new SortingMethod(Methods.BUBBLE, data));
        methods.add(new SortingMethod(Methods.IMPROVED_BUBBLE, data));
        methods.add(new SortingMethod(Methods.OPTIMIZED_BUBBLE, data));
        methods.add(new SortingMethod(Methods.QUICKSORT, data));
        methods.add(new SortingMethod(Methods.SHELLSORT, data));
        methods.add(new SortingMethod(Methods.RADIXSORT, data));
        methods.add(new SortingMethod(Methods.SELECTION, data));
        methods.add(new SortingMethod(Methods.INSERTION, data));
        methods.add(new SortingMethod(Methods.MERGESORT, data));
    }

    public ComparatorMethods() {
        super(new GridBagLayout());
        init();
    }

    private void init() {
        Constrains.addComp(
                new View(methods(), this),
                new Rectangle(0, 0, 1, 1),
                new Weight(1, 1),
                new Insets(5, 5, 30, 5),
                new Point(GridBagConstraints.CENTER, GridBagConstraints.BOTH)
        );
        Constrains.addCompX(
                new View(buttons(), this),
                new Rectangle(0, 1, 1, 1),
                1,
                new Insets(30, 5, 5, 5),
                new Point(GridBagConstraints.CENTER, GridBagConstraints.NONE)
        );
    }

    private JPanel buttons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton runAll = new JButton("Run All");
        JButton restart = new JButton("Restart");
        JButton setValues = new JButton("Set Values");
        panel.add(runAll);
        panel.add(restart);
        panel.add(setValues);
        return panel;
    }

    private JPanel methods() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 20, 20));
        methods.forEach(panel::add);
        return panel;
    }
}