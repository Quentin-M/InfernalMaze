package fr.quentinmachu.infernalmaze.game.objects;

public interface GameObject {
    /**
     * Updates the state (fixed timestep)
     */
    public void update();
    
    /**
     * Renders the state (with interpolation).
     * @param alpha Alpha value, needed for interpolation
     */
    public void render(float alpha);
}
