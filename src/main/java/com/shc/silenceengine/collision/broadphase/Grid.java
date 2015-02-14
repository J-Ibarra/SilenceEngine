package com.shc.silenceengine.collision.broadphase;

import com.shc.silenceengine.entity.Entity2D;
import com.shc.silenceengine.geom2d.Rectangle;
import com.shc.silenceengine.utils.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A Grid based collision resolver. Reduces the number of collision
 * checks and increases performance. This class implements the broad
 * phase collision detection. Though this class is public, you should
 * be using the ISceneCollider2D interface with the scene.
 *
 * @author Sri Harsha Chilakapati
 */
public class Grid implements IBroadphaseResolver2D
{
    // A spatial partitioned structure to hold elements
    private List<List<List<Entity2D>>> grid;

    // Private stuff, self explanatory
    private int rows;
    private int cols;
    private int cellWidth;
    private int cellHeight;

    // A list of short-listed entities
    private List<Entity2D> retrieveList;

    /**
     * Creates and initializes the Grid
     *
     * @param mapWidth  The width of map (in pixels)
     * @param mapHeight The height of map (in pixels)
     * @param cellWidth The width of each cell
     * @param cellHeight The height of each cell
     */
    public Grid(int mapWidth, int mapHeight, int cellWidth, int cellHeight)
    {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;

        rows = (mapHeight + cellHeight - 1) / cellHeight;
        cols = (mapWidth + cellWidth - 1) / cellWidth;

        grid = new ArrayList<>();

        for (int i = 0; i < cols; i++)
        {
            grid.add(new ArrayList<>());

            for (int j = 0; j < rows; j++)
            {
                grid.get(i).add(new ArrayList<>());
            }
        }

        retrieveList = new ArrayList<>();

        clear();
    }

    /**
     * Clears the Grid, removes all entities
     */
    public void clear()
    {
        for (int i = 0; i < cols; i++)
        {
            for (int j = 0; j < rows; j++)
                grid.get(i).get(j).clear();
        }
    }

    /**
     * Simplification method to insert bulk data
     *
     * @param list The list of Entity2Ds
     */
    public void insertAll(List<Entity2D> list)
    {
        for (Entity2D e : list)
            insert(e);
    }

    /**
     * Inserts an entity into the grid, by partitioning the available space
     * @param entity The entity to be added to the grid.
     */
    public void insert(Entity2D entity)
    {
        Rectangle bounds = entity.getPolygon().getBounds();

        int topLeftX = MathUtils.clamp((int) (bounds.getX()) / cellWidth, 0, cols - 1);
        int topLeftY = MathUtils.clamp((int) (bounds.getY()) / cellHeight, 0, rows-1);
        int bottomRightX = MathUtils.clamp((int) (bounds.getX() + bounds.getWidth() - 1) / cellWidth, 0, cols-1);
        int bottomRightY = MathUtils.clamp((int) (bounds.getY() + bounds.getHeight() - 1) / cellHeight, 0, rows-1);

        for (int x = topLeftX; x <= bottomRightX; x++)
        {
            for (int y = topLeftY; y <= bottomRightY; y++)
            {
                grid.get(x).get(y).add(entity);
            }
        }
    }

    /**
     * Removes an entity from the Grid
     * @param entity The entity to be removed
     */
    public void remove(Entity2D entity)
    {
        Rectangle bounds = entity.getPolygon().getBounds();

        int topLeftX = MathUtils.clamp((int) (bounds.getX()) / cellWidth, 0, cols - 1);
        int topLeftY = MathUtils.clamp((int) (bounds.getY()) / cellHeight, 0, rows - 1);
        int bottomRightX = MathUtils.clamp((int) (bounds.getX() + bounds.getWidth() - 1) / cellWidth, 0, cols - 1);
        int bottomRightY = MathUtils.clamp((int) (bounds.getY() + bounds.getHeight() - 1) / cellHeight, 0, rows - 1);

        for (int x = topLeftX; x <= bottomRightX; x++)
        {
            for (int y = topLeftY; y <= bottomRightY; y++)
            {
                if (grid.get(x).get(y).contains(entity))
                    grid.get(x).get(y).remove(entity);
            }
        }
    }

    @Override
    public List<Entity2D> retrieve(Rectangle bounds)
    {
        retrieveList.clear();

        int topLeftX = MathUtils.clamp((int) (bounds.getX()) / cellWidth, 0, cols - 1);
        int topLeftY = MathUtils.clamp((int) (bounds.getY()) / cellHeight, 0, rows - 1);
        int bottomRightX = MathUtils.clamp((int) (bounds.getX() + bounds.getWidth() - 1) / cellWidth, 0, cols - 1);
        int bottomRightY = MathUtils.clamp((int) (bounds.getY() + bounds.getHeight() - 1) / cellHeight, 0, rows - 1);

        for (int x = topLeftX; x <= bottomRightX; x++)
        {
            for (int y = topLeftY; y <= bottomRightY; y++)
            {
                List<Entity2D> cell = grid.get(x).get(y);

                for (Entity2D retrieved : cell)
                    retrieveList.add(retrieved);
            }
        }

        return retrieveList;
    }
}
