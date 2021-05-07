package com.endlessonline.client.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.endlessonline.client.EOGame;
import com.endlessonline.client.io.PacketFactory;
import com.endlessonline.client.renderer.Map;
import com.endlessonline.client.renderer.MapCharacter;
import com.endlessonline.client.world.Direction;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TestScreen implements Screen {

    // literal mess, am using this class for testing code while learning how to make sense of some of what I'm tasked with

    public final BitmapFont font;
    private final Map map;
    public Vector2 mouse = new Vector2();
    public AssetManager manager;
    OrthographicCamera camera;
    OrthographicCamera static_camera;
    String frag, vert;
    ShaderProgram shaderProgram;
    public TextField textArea;

    private float timer = 0;

    private final EOGame game;

    public TestScreen(EOGame game) throws IOException {
        this.game = game;
        font = new BitmapFont();
        map = new Map(this.game.getMap(), this.game);
        this.game.setRenderer(map);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera.position.x = Map.worldToScreenX(70, 70);
        camera.position.y = Map.worldToScreenY(70, 70) - camera.viewportHeight/10;
        static_camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        static_camera.position.set(static_camera.viewportWidth / 2f, static_camera.viewportHeight / 2f, 0);
        manager = new AssetManager();
        frag = Gdx.files.internal("shaders/fragment.glsl").readString();
        vert = frag = Gdx.files.internal("shaders/vertex.glsl").readString();
        shaderProgram = new ShaderProgram(Gdx.files.internal("shaders/vertex.glsl"), Gdx.files.internal("shaders/fragment.glsl"));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        this.timer += delta;
        if (map != null) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            Gdx.gl.glEnable(Gdx.gl20.GL_BLEND);
            game.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            game.batch.totalRenderCalls = 0;

            game.mouse3d.x = Gdx.input.getX();
            game.mouse3d.y = Gdx.input.getY();
            game.mouse3d.z = 0;
            camera.unproject(game.mouse3d);
            mouse.x = game.mouse3d.x;
            mouse.y = game.mouse3d.y;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                if (timer >= 2f) {
                    this.game.send(PacketFactory.walk(Direction.UP, this.game.getWorld().getMainCharacter().getX(), this.game.getWorld().getMainCharacter().getY() - 1));
                    this.timer = 0;
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                camera.position.x -= 32;
                camera.position.y += 16;
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                if (timer >= 2f) {
                    this.game.send(PacketFactory.walk(Direction.DOWN, this.game.getWorld().getMainCharacter().getX(), this.game.getWorld().getMainCharacter().getY() + 1));
                    this.timer = 0;
                }
            } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                camera.position.x += 32;
                camera.position.y -= 16;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (textArea.getText().length() > 1) {
                    this.game.getChat().appendText("\n");
                    this.game.send(PacketFactory.talkLocal(textArea.getText()));
                    this.game.getChat().appendText("[" + LocalTime.now().toString() + this.game.getWorld().getMainCharacter().getName() + ": " + textArea.getText());
                    this.textArea.setText("");
                }
            }

            camera.update();
            game.batch.setProjectionMatrix(camera.combined);
            game.batch.begin();
            game.batch.setShader(shaderProgram);
            if (game.shouldRenderMap) {
                if (map.loaded) {
                    map.drawMap();
                    camera.position.x = map.worldToScreenX(game.getWorld().getMainCharacter().getX(), game.getWorld().getMainCharacter().getY());
                    camera.position.y = map.worldToScreenY(game.getWorld().getMainCharacter().getX(), game.getWorld().getMainCharacter().getY());
                } else {
                    try {
                        map.createMapAssets();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            game.batch.end();

            static_camera.update();

            game.batch.setProjectionMatrix(static_camera.combined);
            game.batch.begin();

            game.batch.setShader(null);
            font.draw(game.batch, "Coordinates: " + (int) Map.screenToWorldX(mouse.x, mouse.y) + ", " + ((int) Map.screenToWorldY(mouse.x, mouse.y) + 1), 24, 18);
            font.draw(game.batch, "FPS: " + game.frameRate, 24, 36);
            font.draw(game.batch, "Render calls: " + game.batch.totalRenderCalls, 24, 54);

            game.batch.end();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        game.batch.dispose();
    }

}
