package kr.mohi.orgjsonlib;

import cn.nukkit.Server;
import cn.nukkit.scheduler.FileWriteTask;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;

/**
 * @author 110EIm
 * @since 2016-07-26
 */
public class JSONConfig {
    private JSONObject config = new JSONObject();
    private File file;

    public JSONConfig(File file) {
        this(file.toString());
    }

    public JSONConfig(File file, JSONObject defaultObject) {
        this(file.toString(), defaultObject);
    }

    public JSONConfig(String file) {
        this(file, new JSONObject());
    }

    public JSONConfig(String file, JSONObject defaultObject) {
        this.load(file, defaultObject);
    }

    public boolean load(String file, JSONObject defaultObject) {
        this.file = new File(file);
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                MainLogger.getLogger().error("Could not create Config " + this.file.toString(), e);
            }
            this.config = defaultObject;
            this.save();
            return true;
        } else {
            String content = "";
            try {
                content = Utils.readFile(this.file);
            } catch (IOException e) {
                Server.getInstance().getLogger().logException(e);
            }
            JSONTokener tokener = new JSONTokener(content);
            try {
                tokener.syntaxError("Syntax error");
            } catch(JSONException e) {
                Server.getInstance().getLogger().logException(e);
            }
            this.config = (JSONObject) tokener.nextValue();
            return true;
        }
    }

    public boolean save(boolean async) {
        if (this.file == null) {
            throw new IllegalStateException("Failed to save JSON file. File object is undefined.");
        }
        String content = this.config.toString();
        if(async) {
            Server.getInstance().getScheduler().scheduleAsyncTask(new FileWriteTask(this.file, content));
            try {
                Utils.writeFile(file, content);
            } catch (IOException e) {
                MainLogger.getLogger().logException(e);
            }
        }
        return true;
    }

    public boolean save() {
        return this.save(false);
    }

    public Object get(String key) {
        return this.config.get(key);
    }

    public JSONObject put(String key, Object value) {
        return this.config.put(key, value);
    }
}
