package GameState;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import GameState.GameStateManager;

import Handlers.Keys;

import Entity.*;
import Entity.Enemies.*;
import Main.GamePanel;
import TileMap.*;
import Audio.JukeBox;

public class Level3State extends GameState{

	private TileMap tileMap;
	private Background bg;
	private Console console;
	private FillScreen fs;
	private DialogBox dbox;
	private DialogBox dbox1;
	private TextPlayer player;
	
	private ArrayList<Enemy> enemies;
	private ArrayList<MovingPlatform> platforms;
	//private ArrayList<Explosion> explosions; 
	private DialogBox[] dialog = {new DialogBox("I see you are still alive...", 2), new DialogBox("You're better than I thought.", 2), new DialogBox("Wh-who are you?", 1), new DialogBox("That... remains a secret.", 2)}; 
	private int index;
	private int index2 = 0;
	private boolean keyPressed;
	
	public Level3State(GameStateManager gsm){
		super(gsm);
		init();
		try {
			gsm.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void init() {
		//initialize tile map
		tileMap = new TileMap(16);
		tileMap.loadTiles("/Tilesets/texttileset.png");
		tileMap.loadMap("/Maps/level1-3.map");
		tileMap.setPosition(0, 0);
		tileMap.setTween(1);
		
		bg = new Background("/Backgrounds/level1bg.png", 0.1);
		populateEnemies();
		player = new TextPlayer(tileMap);
		player.setSpawnPoint(19, 400 - player.getCHeight() / 2);
		player.setPosition(19, 400 - player.getCHeight() / 2);
		//player.setSpawnPoint(51, 380);
		//player.setPosition(51, 380);
		//hud = new HUD(player);
		
		
		JukeBox.load("/Music/level1-1.mp3", "level1");
		//JukeBox.loop("level1", 600, JukeBox.getFrames("level1") - 2200);
		if(!JukeBox.isPlaying("level1")){
			JukeBox.loop("level1", 600, JukeBox.getFrames("level1") - 2200);
		}
		
	}
	
	private void populateEnemies() {
		
		enemies = new ArrayList<Enemy>();
		platforms = new ArrayList<MovingPlatform>();
		
		Slugger s;
		Point[] points = new Point[] {
			new Point(490, 280),
			new Point(474, 380)
		};
		for(int i = 0; i < points.length; i++) {
			s = new Slugger(tileMap);
			s.setPosition(points[i].x, points[i].y);
			enemies.add(s);
		}
		
		MovingPlatform mp;
		Point[] points1 = new Point[] {
				new Point(202, 128)
			};
			for(int i = 0; i < points1.length; i++) {
				mp = new MovingPlatform(tileMap);
				mp.setPosition(points1[i].x, points1[i].y);
				platforms.add(mp);
			}
		
	}
	
	private void printDialogue() {
		if(index < dialog.length){
			dbox = dialog[index];
		}
		
	}

	public void update() {
		// check keys
		
		printDialogue();
		
		if(console == null && index >= dialog.length){
			handleInput();
			player.update();
			
		}
		
		if(Keys.isPressed(Keys.BUTTON1)){
			if(player.tl == Tile.TERMINAL || player.tr == Tile.TERMINAL || player.bl == Tile.TERMINAL || player.br == Tile.TERMINAL){
				if(console == null){
					console = new Console(2);
				}else{
					if(dbox1 == null){
						dbox1 = new DialogBox("", 1);
					}
					if(dbox1.isDone()){
						//gsm.setState(GameStateManager.LEVEL3STATE);
					}
				}
			}
		}
		tileMap.setPosition(GamePanel.WIDTH / 2 - player.getx(), GamePanel.HEIGHT / 2 - player.gety());
		
		
		bg.setPosition(tileMap.getx(), tileMap.gety());
		
		for(int i = 0; i < enemies.size(); i++) {
			Enemy e = enemies.get(i);
			e.update();
		}
		
		for(int i = 0; i < platforms.size(); i++) {
			MovingPlatform mp = platforms.get(i);
			mp.update();
		}
		
		// attack enemies
		player.checkAttack(enemies);
		player.checkPlatformCollision(platforms);
		
		
	}

	
	public void draw(Graphics2D g) {
		
		//draw bg
		bg.draw(g);
		
		//draw tilemap
		tileMap.draw(g);
		
		//draw player
		player.draw(g);
		
		for(int i = 0; i < platforms.size(); i++) {
			platforms.get(i).draw(g);
		}
		
		for(int i = 0; i < enemies.size(); i++) {
			enemies.get(i).draw(g);
		}
		
		if(console != null) console.draw(g);
		//draw hud
		//hud.draw(g);
		try{
			if(!dbox.shouldRemove() && dbox != null){
				dbox.draw(g);
				if(Keys.isPressed(Keys.BUTTON1) && !keyPressed && dbox.isDone()){
					dbox.setRemove(true);
					keyPressed = true;
				}else if(!Keys.isPressed(Keys.BUTTON1)){
					keyPressed = false;
				}
			}
			else if(index < dialog.length){
				index++;
			}
		}catch(Exception e){
		}
	}
	
	public void handleInput() {
		//if(Keys.isPressed(Keys.ESCAPE)) gsm.setPaused(true);
		if(player.getHealth() == 0) return;
		player.setUp(Keys.keyState[Keys.UP]);
		player.setLeft(Keys.keyState[Keys.LEFT]);
		player.setDown(Keys.keyState[Keys.DOWN]);
		player.setRight(Keys.keyState[Keys.RIGHT]);
		player.setJumping(Keys.keyState[Keys.BUTTON1]);
	}

	
	
}