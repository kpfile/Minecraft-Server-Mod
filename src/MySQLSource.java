/* MySQL Data Source */

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class MySQLSource extends DataSource {
    private String driver, username, password, db;
    private Connection conn;

    public void initialize() {
        co properties = new co(new File("mysql.properties"));
        driver = properties.getString("driver", "com.mysql.jdbc.Driver");
        username = properties.getString("user", "root");
        password = properties.getString("pass", "root");
        db = properties.getString("db", "jdbc:mysql://localhost:3306/minecraft");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            log.log(Level.SEVERE, "Unable to find class " + driver, ex);
        }
        try {
            conn = DriverManager.getConnection(db + "?autoReconnect=true&user=" + username + "&password=" + password);
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Unable to retreive connection", ex);
        }

        loadUsers();
        loadGroups();
        loadKits();
        loadHomes();
        loadWarps();
        loadItems();
        loadWhitelist();
        loadReserveList();
    }

    public void loadUsers() {
        synchronized (userLock) {
            users = new ArrayList<User>();
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement("SELECT * FROM users");
                rs = ps.executeQuery();
                while (rs.next()) {
                    User user = new User();
                    user.ID = rs.getInt("id");
                    user.Name = rs.getString("name");
                    user.Groups = rs.getString("groups").split(",");
                    user.Commands = rs.getString("commands").split(",");
                    user.Prefix = rs.getString("prefix");
                    user.Administrator = rs.getBoolean("admin");
                    user.CanModifyWorld = rs.getBoolean("canmodifyworld");
                    user.IgnoreRestrictions = rs.getBoolean("ignoresrestrictions");
                    users.add(user);
                }
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "Unable to retreive users from user table", ex);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                }
            }
        }
    }

    public void loadGroups() {
        synchronized (groupLock) {
            groups = new ArrayList<Group>();
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement("SELECT * FROM groups");
                rs = ps.executeQuery();
                while (rs.next()) {
                    Group group = new Group();
                    group.Administrator = rs.getBoolean("admin");
                    group.CanModifyWorld = rs.getBoolean("canmodifyworld");
                    group.Commands = rs.getString("commands").split(",");
                    group.DefaultGroup = rs.getBoolean("defaultgroup");
                    group.ID = rs.getInt("id");
                    group.IgnoreRestrictions = rs.getBoolean("ignoresrestrictions");
                    group.InheritedGroups = rs.getString("inheritedgroups").split(",");
                    group.Name = rs.getString("name");
                    group.Prefix = rs.getString("prefix");
                    groups.add(group);
                }
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "Unable to retreive groups from group table", ex);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                }
            }
        }
    }

    public void loadKits() {
        synchronized (kitLock) {
            kits = new ArrayList<Kit>();
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement("SELECT * FROM kits");
                rs = ps.executeQuery();
                while (rs.next()) {
                    Kit kit = new Kit();
                    kit.Delay = rs.getInt("delay");
                    kit.Group = rs.getString("group");
                    kit.ID = rs.getInt("id");
                    kit.Name = rs.getString("name");
                    kit.IDs = new HashMap<String, Integer>();

                    String[] ids = rs.getString("items").split(",");
                    for (String str : ids) {
                        String id = "";
                        int amount = 1;
                        if (str.contains(" ")) {
                            id = str.split(" ")[0];
                            amount = Integer.parseInt(str.split(" ")[1]);
                        } else {
                            id = str;
                        }
                        kit.IDs.put(id, amount);
                    }
                    kits.add(kit);
                }
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "Unable to retreive kits from kit table", ex);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                }
            }
        }
    }

    public void loadHomes() {
        synchronized (homeLock) {
            homes = new HashMap<String, Location>();
            if (!etc.getInstance().saveHomes) {
                return;
            }
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement("SELECT * FROM homes");
                rs = ps.executeQuery();
                while (rs.next()) {
                    Location location = new Location();
                    location.x = rs.getDouble("x");
                    location.y = rs.getDouble("y");
                    location.z = rs.getDouble("z");
                    location.rotX = rs.getFloat("rotX");
                    location.rotY = rs.getFloat("rotY");
                    location.ID = rs.getInt("id");
                    homes.put(rs.getString("name"), location);
                }
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "Unable to retreive homes from home table", ex);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                }
            }
        }
    }

    public void loadWarps() {
        synchronized (warpLock) {
            warps = new HashMap<String, Location>();
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement("SELECT * FROM warps");
                rs = ps.executeQuery();
                while (rs.next()) {
                    Location location = new Location();
                    location.x = rs.getDouble("x");
                    location.y = rs.getDouble("y");
                    location.z = rs.getDouble("z");
                    location.rotX = rs.getFloat("rotX");
                    location.rotY = rs.getFloat("rotY");
                    location.ID = rs.getInt("id");
                    warps.put(rs.getString("name"), location);
                }
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "Unable to retreive warps from warp table", ex);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                }
            }
        }
    }

    public void loadItems() {
        synchronized (itemLock) {
            items = new HashMap<String, Integer>();
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement("SELECT * FROM items");
                rs = ps.executeQuery();
                while (rs.next()) {
                    items.put(rs.getString("name"), rs.getInt("itemid"));
                }
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "Unable to retreive items from item table", ex);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                }
            }
        }
    }

    public void loadWhitelist() {
        synchronized (whiteListLock) {
            whiteList = new ArrayList<String>();
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement("SELECT * FROM whitelist");
                rs = ps.executeQuery();
                while (rs.next()) {
                    whiteList.add(rs.getString(1));
                }
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "Unable to retreive users from whitelist table", ex);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                }
            }
        }
    }

    public void loadReserveList() {
        synchronized (reserveListLock) {
            reserveList = new ArrayList<String>();
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = conn.prepareStatement("SELECT * FROM reservelist");
                rs = ps.executeQuery();
                while (rs.next()) {
                    reserveList.add(rs.getString(1));
                }
            } catch (SQLException ex) {
                log.log(Level.SEVERE, "Unable to retreive users from whitelist table", ex);
            } finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                }
            }
        }
    }

    //Users
    public void addUser(User user) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("INSERT INTO users (name, groups, prefix, commands, admin, canmodifyworld, ignoresrestrictions) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.Name);
            ps.setString(2, ia.combineSplit(0, user.Groups, ","));
            ps.setString(3, user.Prefix);
            ps.setString(4, ia.combineSplit(0, user.Commands, ","));
            ps.setBoolean(5, user.Administrator);
            ps.setBoolean(6, user.CanModifyWorld);
            ps.setBoolean(7, user.IgnoreRestrictions);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                user.ID = rs.getInt(1);
                synchronized (userLock) {
                    users.add(user);
                }
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Unable to insert user into users table", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void modifyUser(User user) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("UPDATE users SET groups = ?, prefix = ?, commands = ?, admin = ?, canmodifyworld = ?, ignoresrestrictions = ? WHERE id = ?");
            ps.setString(1, ia.combineSplit(0, user.Groups, ","));
            ps.setString(2, user.Prefix);
            ps.setString(3, ia.combineSplit(0, user.Commands, ","));
            ps.setBoolean(4, user.Administrator);
            ps.setBoolean(5, user.CanModifyWorld);
            ps.setBoolean(6, user.IgnoreRestrictions);
            ps.setInt(7, user.ID);
            ps.executeUpdate();
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Unable to update user in users table", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    //Groups
    public void addGroup(Group group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void modifyGroup(Group group) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Kits
    public void addKit(Kit kit) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void modifyKit(Kit kit) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //Homes
    public void addHome(String name, Location location) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("INSERT INTO homes (name, x, y, z, rotX, rotY) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setDouble(2, location.x);
            ps.setDouble(3, location.y);
            ps.setDouble(4, location.z);
            ps.setFloat(5, location.rotX);
            ps.setFloat(6, location.rotY);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                location.ID = rs.getInt(1);
                synchronized (homeLock) {
                    homes.put(name, location);
                }
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Unable to insert home into homes table", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void changeHome(String name, Location location) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("UPDATE homes SET x = ?, y = ?, z = ?, rotX = ?, rotY = ? WHERE name = ?");
            ps.setDouble(1, location.x);
            ps.setDouble(2, location.y);
            ps.setDouble(3, location.z);
            ps.setFloat(4, location.rotX);
            ps.setFloat(5, location.rotY);
            ps.setString(6, name);
            ps.executeUpdate();
            synchronized (homeLock) {
                homes.put(name, location);
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Unable to update home in homes table", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    //Warps
    public void addWarp(String name, Location location) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("INSERT INTO warps (name, x, y, z, rotX, rotY) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setDouble(2, location.x);
            ps.setDouble(3, location.y);
            ps.setDouble(4, location.z);
            ps.setFloat(5, location.rotX);
            ps.setFloat(6, location.rotY);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                location.ID = rs.getInt(1);
                synchronized (warpLock) {
                    warps.put(name, location);
                }
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Unable to insert warp into warps table", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void changeWarp(String name, Location location) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("UPDATE warps SET x = ?, y = ?, z = ?, rotX = ?, rotY = ? WHERE name = ?");
            ps.setDouble(1, location.x);
            ps.setDouble(2, location.y);
            ps.setDouble(3, location.z);
            ps.setFloat(4, location.rotX);
            ps.setFloat(5, location.rotY);
            ps.setString(6, name);
            ps.executeUpdate();
            synchronized (warpLock) {
                warps.put(name, location);
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Unable to update warp in warps table", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    //Whitelist
    public void addToWhitelist(String name) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("INSERT INTO whitelist VALUES(?)");
            ps.setString(1, name);
            ps.executeUpdate();
            synchronized (whiteListLock) {
                whiteList.add(name);
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Unable to update whitelist", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void removeFromWhitelist(String name) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("DELETE FROM whitelist WHERE name = ?");
            ps.setString(1, name);
            ps.executeUpdate();
            synchronized (whiteListLock) {
                whiteList.add(name);
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Unable to update whitelist", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    //Reservelist
    public void addToReserveList(String name) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("INSERT INTO reservelist VALUES(?)");
            ps.setString(1, name);
            ps.executeUpdate();
            synchronized (reserveListLock) {
                reserveList.add(name);
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Unable to update reservelist", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void removeFromReserveList(String name) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement("DELETE FROM reservelist WHERE name = ?");
            ps.setString(1, name);
            ps.executeUpdate();
            synchronized (reserveListLock) {
                reserveList.add(name);
            }
        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Unable to update reservelist", ex);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
    }
}
