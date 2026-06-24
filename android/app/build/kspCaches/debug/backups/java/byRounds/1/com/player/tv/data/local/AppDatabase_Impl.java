package com.player.tv.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile PlaylistDao _playlistDao;

  private volatile ChannelDao _channelDao;

  private volatile FavoriteDao _favoriteDao;

  private volatile RecentDao _recentDao;

  private volatile EpgDao _epgDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `playlists` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `url` TEXT, `filePath` TEXT, `type` TEXT NOT NULL, `channelCount` INTEGER NOT NULL, `lastUpdated` INTEGER NOT NULL, `epgUrl` TEXT, `username` TEXT, `password` TEXT, `serverUrl` TEXT, `macAddress` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `channels` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `url` TEXT NOT NULL, `logo` TEXT, `groupTitle` TEXT, `tvgId` TEXT, `tvgName` TEXT, `isRadio` INTEGER NOT NULL, `streamFormat` TEXT NOT NULL, `streamType` TEXT NOT NULL, `playlistId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`playlistId`) REFERENCES `playlists`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_channels_playlistId` ON `channels` (`playlistId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_channels_groupTitle` ON `channels` (`groupTitle`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `favorites` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `channelId` TEXT NOT NULL, `playlistId` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `recent` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `channelId` TEXT NOT NULL, `playlistId` TEXT NOT NULL, `channelName` TEXT NOT NULL, `channelLogo` TEXT, `watchedAt` INTEGER NOT NULL, `playbackPosition` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `epg_programs` (`id` TEXT NOT NULL, `channelId` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT, `startTime` INTEGER NOT NULL, `endTime` INTEGER NOT NULL, `icon` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c9c69b2ae4d986cdb69c67c05e929cff')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `playlists`");
        db.execSQL("DROP TABLE IF EXISTS `channels`");
        db.execSQL("DROP TABLE IF EXISTS `favorites`");
        db.execSQL("DROP TABLE IF EXISTS `recent`");
        db.execSQL("DROP TABLE IF EXISTS `epg_programs`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsPlaylists = new HashMap<String, TableInfo.Column>(12);
        _columnsPlaylists.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("url", new TableInfo.Column("url", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("filePath", new TableInfo.Column("filePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("channelCount", new TableInfo.Column("channelCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("lastUpdated", new TableInfo.Column("lastUpdated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("epgUrl", new TableInfo.Column("epgUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("username", new TableInfo.Column("username", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("password", new TableInfo.Column("password", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("serverUrl", new TableInfo.Column("serverUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("macAddress", new TableInfo.Column("macAddress", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlaylists = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPlaylists = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPlaylists = new TableInfo("playlists", _columnsPlaylists, _foreignKeysPlaylists, _indicesPlaylists);
        final TableInfo _existingPlaylists = TableInfo.read(db, "playlists");
        if (!_infoPlaylists.equals(_existingPlaylists)) {
          return new RoomOpenHelper.ValidationResult(false, "playlists(com.player.tv.data.local.PlaylistEntity).\n"
                  + " Expected:\n" + _infoPlaylists + "\n"
                  + " Found:\n" + _existingPlaylists);
        }
        final HashMap<String, TableInfo.Column> _columnsChannels = new HashMap<String, TableInfo.Column>(12);
        _columnsChannels.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("url", new TableInfo.Column("url", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("logo", new TableInfo.Column("logo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("groupTitle", new TableInfo.Column("groupTitle", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("tvgId", new TableInfo.Column("tvgId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("tvgName", new TableInfo.Column("tvgName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("isRadio", new TableInfo.Column("isRadio", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("streamFormat", new TableInfo.Column("streamFormat", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("streamType", new TableInfo.Column("streamType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("playlistId", new TableInfo.Column("playlistId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsChannels.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChannels = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysChannels.add(new TableInfo.ForeignKey("playlists", "CASCADE", "NO ACTION", Arrays.asList("playlistId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesChannels = new HashSet<TableInfo.Index>(2);
        _indicesChannels.add(new TableInfo.Index("index_channels_playlistId", false, Arrays.asList("playlistId"), Arrays.asList("ASC")));
        _indicesChannels.add(new TableInfo.Index("index_channels_groupTitle", false, Arrays.asList("groupTitle"), Arrays.asList("ASC")));
        final TableInfo _infoChannels = new TableInfo("channels", _columnsChannels, _foreignKeysChannels, _indicesChannels);
        final TableInfo _existingChannels = TableInfo.read(db, "channels");
        if (!_infoChannels.equals(_existingChannels)) {
          return new RoomOpenHelper.ValidationResult(false, "channels(com.player.tv.data.local.ChannelEntity).\n"
                  + " Expected:\n" + _infoChannels + "\n"
                  + " Found:\n" + _existingChannels);
        }
        final HashMap<String, TableInfo.Column> _columnsFavorites = new HashMap<String, TableInfo.Column>(4);
        _columnsFavorites.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("channelId", new TableInfo.Column("channelId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("playlistId", new TableInfo.Column("playlistId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFavorites.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFavorites = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFavorites = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFavorites = new TableInfo("favorites", _columnsFavorites, _foreignKeysFavorites, _indicesFavorites);
        final TableInfo _existingFavorites = TableInfo.read(db, "favorites");
        if (!_infoFavorites.equals(_existingFavorites)) {
          return new RoomOpenHelper.ValidationResult(false, "favorites(com.player.tv.data.local.FavoriteEntity).\n"
                  + " Expected:\n" + _infoFavorites + "\n"
                  + " Found:\n" + _existingFavorites);
        }
        final HashMap<String, TableInfo.Column> _columnsRecent = new HashMap<String, TableInfo.Column>(7);
        _columnsRecent.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecent.put("channelId", new TableInfo.Column("channelId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecent.put("playlistId", new TableInfo.Column("playlistId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecent.put("channelName", new TableInfo.Column("channelName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecent.put("channelLogo", new TableInfo.Column("channelLogo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecent.put("watchedAt", new TableInfo.Column("watchedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecent.put("playbackPosition", new TableInfo.Column("playbackPosition", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecent = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecent = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRecent = new TableInfo("recent", _columnsRecent, _foreignKeysRecent, _indicesRecent);
        final TableInfo _existingRecent = TableInfo.read(db, "recent");
        if (!_infoRecent.equals(_existingRecent)) {
          return new RoomOpenHelper.ValidationResult(false, "recent(com.player.tv.data.local.RecentEntity).\n"
                  + " Expected:\n" + _infoRecent + "\n"
                  + " Found:\n" + _existingRecent);
        }
        final HashMap<String, TableInfo.Column> _columnsEpgPrograms = new HashMap<String, TableInfo.Column>(7);
        _columnsEpgPrograms.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEpgPrograms.put("channelId", new TableInfo.Column("channelId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEpgPrograms.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEpgPrograms.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEpgPrograms.put("startTime", new TableInfo.Column("startTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEpgPrograms.put("endTime", new TableInfo.Column("endTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEpgPrograms.put("icon", new TableInfo.Column("icon", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEpgPrograms = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEpgPrograms = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEpgPrograms = new TableInfo("epg_programs", _columnsEpgPrograms, _foreignKeysEpgPrograms, _indicesEpgPrograms);
        final TableInfo _existingEpgPrograms = TableInfo.read(db, "epg_programs");
        if (!_infoEpgPrograms.equals(_existingEpgPrograms)) {
          return new RoomOpenHelper.ValidationResult(false, "epg_programs(com.player.tv.data.local.EpgProgramEntity).\n"
                  + " Expected:\n" + _infoEpgPrograms + "\n"
                  + " Found:\n" + _existingEpgPrograms);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "c9c69b2ae4d986cdb69c67c05e929cff", "28f0ddf1eb721c2d2407191d2a675edb");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "playlists","channels","favorites","recent","epg_programs");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `playlists`");
      _db.execSQL("DELETE FROM `channels`");
      _db.execSQL("DELETE FROM `favorites`");
      _db.execSQL("DELETE FROM `recent`");
      _db.execSQL("DELETE FROM `epg_programs`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(PlaylistDao.class, PlaylistDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ChannelDao.class, ChannelDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FavoriteDao.class, FavoriteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RecentDao.class, RecentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EpgDao.class, EpgDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public PlaylistDao playlistDao() {
    if (_playlistDao != null) {
      return _playlistDao;
    } else {
      synchronized(this) {
        if(_playlistDao == null) {
          _playlistDao = new PlaylistDao_Impl(this);
        }
        return _playlistDao;
      }
    }
  }

  @Override
  public ChannelDao channelDao() {
    if (_channelDao != null) {
      return _channelDao;
    } else {
      synchronized(this) {
        if(_channelDao == null) {
          _channelDao = new ChannelDao_Impl(this);
        }
        return _channelDao;
      }
    }
  }

  @Override
  public FavoriteDao favoriteDao() {
    if (_favoriteDao != null) {
      return _favoriteDao;
    } else {
      synchronized(this) {
        if(_favoriteDao == null) {
          _favoriteDao = new FavoriteDao_Impl(this);
        }
        return _favoriteDao;
      }
    }
  }

  @Override
  public RecentDao recentDao() {
    if (_recentDao != null) {
      return _recentDao;
    } else {
      synchronized(this) {
        if(_recentDao == null) {
          _recentDao = new RecentDao_Impl(this);
        }
        return _recentDao;
      }
    }
  }

  @Override
  public EpgDao epgDao() {
    if (_epgDao != null) {
      return _epgDao;
    } else {
      synchronized(this) {
        if(_epgDao == null) {
          _epgDao = new EpgDao_Impl(this);
        }
        return _epgDao;
      }
    }
  }
}
