package com.justusl;

import java.awt.Color;
import java.util.ArrayList;

public class Screen {
    private int[][] map;
    private int mapWidth, mapHeight, width, height;
    private ArrayList<Texture> textures;

    public Screen(int[][] map, ArrayList<Texture> texture,
            int width, int height) {
        this.map = map;
        mapWidth = map[0].length;
        mapHeight = map.length;
        this.textures = texture;
        this.width = width;
        this.height = height;
    }

    public int[] update(Camera camera, int[] pixels) {
        for (int n = 0; n < pixels.length / 2; n++) {
            if(pixels[n] != Color.DARK_GRAY.getRGB()) pixels[n] = Color.DARK_GRAY.getRGB();
        }
        for (int i = pixels.length / 2; i < pixels.length; i++) {
            if(pixels[i] != Color.gray.getRGB()) pixels[i] = Color.gray.getRGB();
        }

        for (int x = 0; x < width; x++) {
            double cameraX = 2 * x / (double) width - 1;
            double rayDirX = camera.xDir + camera.xPlane * cameraX;
            double rayDirY = camera.yDir + camera.yPlane * cameraX;

            int mapX = (int)camera.xPos;
            int mapY = (int)camera.yPos;

            double sideDistX;
            double sideDistY;

            double deltaDistX = Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
            double deltaDistY = Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));
            double perpWallDist;

            int stepX, stepY;
            boolean hit = false;
            int side = 0;

            if (rayDirX < 0) {
                stepX = -1;
                sideDistX = (camera.xPos - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1. - camera.xPos) * deltaDistX;
            }

            if (rayDirY < 0) {
                stepY = -1;
                sideDistY = (camera.yPos - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1. - camera.yPos) * deltaDistY;
            }

            while (!hit) {
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }
                if (map[mapX][mapY] > 0) hit = true;
            }

            if (side == 0) perpWallDist = Math.abs((mapX - camera.xPos + (1 - stepX) / 2) / rayDirX);
            else perpWallDist = Math.abs((mapY - camera.yPos + (1 - stepY) / 2) / rayDirY);    

            int lineHeight;
            if (perpWallDist > 0) lineHeight = Math.abs((int)(height / perpWallDist));
            else lineHeight = height;

            int drawStart = -lineHeight/2+ height/2;
            if (drawStart < 0) drawStart = 0;

            int drawEnd = lineHeight/2 + height/2;
            if (drawEnd >= height) drawEnd = height - 1;

            int texNum = map[mapX][mapY] - 1;
            double wallX;
            if (side == 1) wallX = (camera.xPos + ((mapY - camera.yPos + (1 - stepY) / 2) / rayDirY) * rayDirX);
            else wallX = (camera.yPos + ((mapX - camera.xPos + (1 - stepX) / 2) / rayDirX) * rayDirY);

            wallX -= Math.floor(wallX);
            int texX = (int) (wallX * (textures.get(texNum).getSize()));
            if (side == 0 && rayDirX > 0) texX = textures.get(texNum).getSize() - texX - 1;
            if (side == 1 && rayDirY < 0) texX = textures.get(texNum).getSize() - texX - 1;

            for(int y = drawStart; y < drawEnd; y++) {
                int texY = (((y * 2 - height + lineHeight) << 6) / lineHeight) / 2;
                int color;

                if (side == 0) color = textures.get(texNum).getPixels()[texX 
                        + (texY * textures.get(texNum).getSize())];
                else color = (textures.get(texNum).getPixels()[texX + (texY 
                        * textures.get(texNum).getSize())] >> 1) & 8355711;

                pixels[x + y * (width)] = color;
            }
        }

        return pixels;
    }
}
