/* Interface so we can either use MySQL or flat files */

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.MinecraftServer;

/**
 * DataSource is the abstract class for implementing new data sources.
 * @author James
 */
public abstract class DataSource {
    
    protected static final Logger log = Logger.getLogger("Minecraft");
    protected List<User> users;
    protected List<String> reserveList;
    protected List<String> whiteList;
    protected List<Group> groups;
    protected List<Kit> kits;
    protected List<Warp> homes;
    protected List<Warp> warps;
    protected Map<String, Integer> items;
    protected MinecraftServer server;
    protected final Object userLock = new Object(), groupLock = new Object(), kitLock = new Object();
    protected final Object homeLock = new Object(), warpLock = new Object(), itemLock = new Object();
    protected final Object whiteListLock = new Object(), reserveListLock = new Object();

    /**
     * Initializes the data source
     */
    abstract public void initialize();

    /**
     * Loads all users
     */
    abstract public void loadUsers();

    /**
     * Loads all groups
     */
    abstract public void loadGroups();

    /**
     * Loads all kits
     */
    abstract public void loadKits();

    /**
     * Loads all homes
     */
    abstract public void loadHomes();

    /**
     * Loads all warps
     */
    abstract public void loadWarps();

    /**
     * Loads all items
     */
    abstract public void loadItems();

    /**
     * Loads the whitelist
     */
    abstract public void loadWhitelist();

    /**
     * Loads the reservelist
     */
    abstract public void loadReserveList();

    //abstract public void loadBanList();

    /**
     * Adds user to the list
     * @param user
     */
    abstract public void addUser(User user);

    /**
     * Modifies the provided user
     * @param user
     */
    abstract public void modifyUser(User user);

    /**
     * Returns specified user
     * @param name
     * @return user
     */
    public User getUser(String name) {
        synchronized (userLock) {
            for (User user : users) {
                if (user.Name.equalsIgnoreCase(name)) {
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Adds specified group to the list of groups
     * @param group
     */
    abstract public void addGroup(Group group);

    /**
     * Modifies group
     * @param group
     */
    abstract public void modifyGroup(Group group);

    /**
     * Returns specified group
     * @param name
     * @return group
     */
    public Group getGroup(String name) {
        synchronized (groupLock) {
            for (Group group : groups) {
                if (group.Name.equalsIgnoreCase(name)) {
                    return group;
                }
            }
        }

        if (!name.equals("")) {
            log.log(Level.INFO, "Unable to find group '" + name + "'. Are you sure you have that group?");
        }

        return null;
    }

    /**
     * Returns the default group
     * @return default group
     */
    public Group getDefaultGroup() {
        synchronized (groupLock) {
            for (Group group : groups) {
                if (group.DefaultGroup) {
                    return group;
                }
            }
        }
        return null;
    }

    /**
     * Adds kit to list of kits
     * @param kit
     */
    abstract public void addKit(Kit kit);

    /**
     * Modifies kit
     * @param kit
     */
    abstract public void modifyKit(Kit kit);

    /**
     * Returns specified kit
     * @param name
     * @return kit
     */
    public Kit getKit(String name) {
        synchronized (kitLock) {
            for (Kit kit : kits) {
                if (kit.Name.equalsIgnoreCase(name)) {
                    return kit;
                }
            }
        }
        return null;
    }

    /**
     * Returns true if there are any kits
     * @return true if there are kits
     */
    public boolean hasKits() {
        synchronized (kitLock) {
            return kits.size() > 0;
        }
    }

    /**
     * Returns a list of all kits names seperated by commas
     * @param player
     * @return string list of kits
     */
    public String getKitNames(String player) {
        StringBuilder builder = new StringBuilder();
        builder.append(""); //incaseofnull

        synchronized (kitLock) {
            for (Kit kit : kits) {
                if (etc.getInstance().isUserInGroup(player, kit.Group) || kit.Group.equals("")) {
                    builder.append(kit.Name).append(" ");
                }
            }
        }

        return builder.toString();
    }

    /**
     * Adds home to list of homes
     * @param home
     */
    abstract public void addHome(Warp home);

    /**
     * Modifies home
     * @param home
     */
    abstract public void changeHome(Warp home);

    /**
     * Returns specified home
     * @param name
     * @return home
     */
    public Warp getHome(String name) {
        synchronized (homeLock) {
            for (Warp home : homes) {
                if (home.Name.equalsIgnoreCase(name)) {
                    return home;
                }
            }
        }
        return null;
    }

    /**
     * Adds warp to list of warps
     * @param warp
     */
    abstract public void addWarp(Warp warp);

    /**
     * Modifies warp
     * @param warp
     */
    abstract public void changeWarp(Warp warp);

    /**
     * Removes warp from list of warps
     * @param warp
     */
    abstract public void removeWarp(Warp warp);

    /**
     * Returns specified warp
     * @param name
     * @return warp
     */
    public Warp getWarp(String name) {
        synchronized (warpLock) {
            for (Warp warp : warps) {
                if (warp.Name.equalsIgnoreCase(name)) {
                    return warp;
                }
            }
        }
        return null;
    }

    /**
     * Returns true if there are any warps
     * @return true if there are warps
     */
    public boolean hasWarps() {
        synchronized (warpLock) {
            return warps.size() > 0;
        }
    }

    /**
     * Returns a string containing all warp names the player has access to
     * @param player
     * @return string list of warps
     */
    public String getWarpNames(String player) {
        StringBuilder builder = new StringBuilder();
        builder.append(""); //incaseofnull

        synchronized (warpLock) {
            for (Warp warp : warps) {
                if (etc.getInstance().isUserInGroup(player, warp.Group) || warp.Group.equals("")) {
                    builder.append(warp.Name).append(" ");
                }
            }
        }

        return builder.toString();
    }

    /**
     * Returns item id corresponding to item name
     * @param name
     * @return item id
     */
    public int getItem(String name) {
        synchronized (itemLock) {
            if (items.containsKey(name)) {
                return items.get(name);
            }
        }
        return 0;
    }

    /**
     * Adds player to whitelist
     * @param name
     */
    abstract public void addToWhitelist(String name);

    /**
     * Removes player from whitelist
     * @param name
     */
    abstract public void removeFromWhitelist(String name);

    /**
     * returns true if there is a whitelist
     * @return true if whitelist
     */
    public boolean hasWhitelist() {
        synchronized (whiteListLock) {
            return !whiteList.isEmpty();
        }
    }

    /**
     * Returns true if the player is on the whitelist
     * @param user
     * @return
     */
    public boolean isUserOnWhitelist(String user) {
        synchronized (whiteListLock) {
            for (String name : whiteList) {
                if (name.equalsIgnoreCase(user)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Adds player to reservelist
     * @param name
     */
    abstract public void addToReserveList(String name);

    /**
     * Removes player from reservelist
     * @param name
     */
    abstract public void removeFromReserveList(String name);

    /**
     * Returns true if there is a reservelist
     * @return true if reservelist
     */
    public boolean hasReserveList() {
        synchronized (reserveList) {
            return !reserveList.isEmpty();
        }
    }

    /**
     * Returns true if player is on reservelist
     * @param user
     * @return
     */
    public boolean isUserOnReserveList(String user) {
        synchronized (reserveList) {
            for (String name : reserveList) {
                if (name.equalsIgnoreCase(user)) {
                    return true;
                }
            }
        }
        return false;
    }
}
