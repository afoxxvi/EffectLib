package de.slikey.effectlib.effect;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.math.EquationStore;
import de.slikey.effectlib.math.EquationTransform;
import de.slikey.effectlib.util.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlotEffect extends Effect {
    private final static String[] _variables = {"t", "i"};
    private final static Set<String> variables = new HashSet<String>(Arrays.asList(_variables));

    /**
     * ParticleType of spawned particle
     */
    public ParticleEffect particle = ParticleEffect.REDSTONE;

    /**
     * The base configuration of the inner effect.
     */
    public ConfigurationSection effect;

    /**
     * The equation to use for x-values. If not set, the iteration count will be used.
     */
    public String xEquation;

    /**
     * The equation to use for y-values. If not set, the iteration count will be used.
     */
    public String yEquation;

    /**
     * The equation to use for y-values. If not set, 0 will be used.
     */
    public String zEquation;

    /**
     * This is a shortcut to quickly scaling the x value.
     */
    public double xScale = 1.0;

    /**
     * This is a shortcut to quickly scaling the y value.
     */
    public double yScale = 1.0;

    /**
     * This is a shortcut to quickly scaling the z value.
     */
    public double zScale = 1.0;

    /**
     * This will re-spawn particles as the plot moves to make a solid line.
     */
    public boolean persistent = true;

    /**
     * Effect parameters to modify each tick, paired with an equation used to modify them.
     */
    public Map<String, String> parameters = new HashMap<String, String>();

    public PlotEffect(EffectManager effectManager) {
        super(effectManager);
        type = EffectType.REPEATING;
        period = 1;
        iterations = 100;
    }

    private int step = 0;

    @Override
    public void onRun() {
        int base = persistent ? 0 : step;
        for (int i = base; i <= step; i++) {
            Location location = getLocation().clone();
            double xOffset = step;
            double yOffset = step;
            double zOffset = 0;

            if (xEquation != null && !xEquation.isEmpty()) {
                EquationTransform xTransform = EquationStore.getInstance().getTransform(xEquation, variables);
                xOffset = xTransform.get(i, maxIterations);
            }

            if (yEquation != null && !yEquation.isEmpty()) {
                EquationTransform yTransform = EquationStore.getInstance().getTransform(yEquation, variables);
                yOffset = yTransform.get(i, maxIterations);
            }

            if (zEquation != null && !zEquation.isEmpty()) {
                EquationTransform zTransform = EquationStore.getInstance().getTransform(zEquation, variables);
                zOffset = zTransform.get(i, maxIterations);
            }

            location.add(xOffset * xScale, yOffset * yScale, zOffset * zScale);
            display(particle, location);
        }

        step++;
    }
}
