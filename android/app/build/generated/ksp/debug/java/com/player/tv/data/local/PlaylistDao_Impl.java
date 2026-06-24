package com.player.tv.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PlaylistDao_Impl implements PlaylistDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PlaylistEntity> __insertionAdapterOfPlaylistEntity;

  private final EntityDeletionOrUpdateAdapter<PlaylistEntity> __deletionAdapterOfPlaylistEntity;

  private final EntityDeletionOrUpdateAdapter<PlaylistEntity> __updateAdapterOfPlaylistEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeletePlaylistById;

  public PlaylistDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPlaylistEntity = new EntityInsertionAdapter<PlaylistEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `playlists` (`id`,`name`,`url`,`filePath`,`type`,`channelCount`,`lastUpdated`,`epgUrl`,`username`,`password`,`serverUrl`,`macAddress`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PlaylistEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getUrl() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getUrl());
        }
        if (entity.getFilePath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getFilePath());
        }
        statement.bindString(5, entity.getType());
        statement.bindLong(6, entity.getChannelCount());
        statement.bindLong(7, entity.getLastUpdated());
        if (entity.getEpgUrl() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getEpgUrl());
        }
        if (entity.getUsername() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getUsername());
        }
        if (entity.getPassword() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getPassword());
        }
        if (entity.getServerUrl() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getServerUrl());
        }
        if (entity.getMacAddress() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getMacAddress());
        }
      }
    };
    this.__deletionAdapterOfPlaylistEntity = new EntityDeletionOrUpdateAdapter<PlaylistEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `playlists` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PlaylistEntity entity) {
        statement.bindString(1, entity.getId());
      }
    };
    this.__updateAdapterOfPlaylistEntity = new EntityDeletionOrUpdateAdapter<PlaylistEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `playlists` SET `id` = ?,`name` = ?,`url` = ?,`filePath` = ?,`type` = ?,`channelCount` = ?,`lastUpdated` = ?,`epgUrl` = ?,`username` = ?,`password` = ?,`serverUrl` = ?,`macAddress` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PlaylistEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getUrl() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getUrl());
        }
        if (entity.getFilePath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getFilePath());
        }
        statement.bindString(5, entity.getType());
        statement.bindLong(6, entity.getChannelCount());
        statement.bindLong(7, entity.getLastUpdated());
        if (entity.getEpgUrl() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getEpgUrl());
        }
        if (entity.getUsername() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getUsername());
        }
        if (entity.getPassword() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getPassword());
        }
        if (entity.getServerUrl() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getServerUrl());
        }
        if (entity.getMacAddress() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getMacAddress());
        }
        statement.bindString(13, entity.getId());
      }
    };
    this.__preparedStmtOfDeletePlaylistById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM playlists WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertPlaylist(final PlaylistEntity playlist,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPlaylistEntity.insert(playlist);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePlaylist(final PlaylistEntity playlist,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfPlaylistEntity.handle(playlist);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePlaylist(final PlaylistEntity playlist,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPlaylistEntity.handle(playlist);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePlaylistById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePlaylistById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeletePlaylistById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PlaylistEntity>> getAllPlaylists() {
    final String _sql = "SELECT * FROM playlists ORDER BY lastUpdated DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"playlists"}, new Callable<List<PlaylistEntity>>() {
      @Override
      @NonNull
      public List<PlaylistEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfChannelCount = CursorUtil.getColumnIndexOrThrow(_cursor, "channelCount");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final int _cursorIndexOfEpgUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "epgUrl");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
          final int _cursorIndexOfServerUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "serverUrl");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final List<PlaylistEntity> _result = new ArrayList<PlaylistEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlaylistEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpUrl;
            if (_cursor.isNull(_cursorIndexOfUrl)) {
              _tmpUrl = null;
            } else {
              _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final int _tmpChannelCount;
            _tmpChannelCount = _cursor.getInt(_cursorIndexOfChannelCount);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            final String _tmpEpgUrl;
            if (_cursor.isNull(_cursorIndexOfEpgUrl)) {
              _tmpEpgUrl = null;
            } else {
              _tmpEpgUrl = _cursor.getString(_cursorIndexOfEpgUrl);
            }
            final String _tmpUsername;
            if (_cursor.isNull(_cursorIndexOfUsername)) {
              _tmpUsername = null;
            } else {
              _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            }
            final String _tmpPassword;
            if (_cursor.isNull(_cursorIndexOfPassword)) {
              _tmpPassword = null;
            } else {
              _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
            }
            final String _tmpServerUrl;
            if (_cursor.isNull(_cursorIndexOfServerUrl)) {
              _tmpServerUrl = null;
            } else {
              _tmpServerUrl = _cursor.getString(_cursorIndexOfServerUrl);
            }
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            _item = new PlaylistEntity(_tmpId,_tmpName,_tmpUrl,_tmpFilePath,_tmpType,_tmpChannelCount,_tmpLastUpdated,_tmpEpgUrl,_tmpUsername,_tmpPassword,_tmpServerUrl,_tmpMacAddress);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getPlaylistById(final String id,
      final Continuation<? super PlaylistEntity> $completion) {
    final String _sql = "SELECT * FROM playlists WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PlaylistEntity>() {
      @Override
      @Nullable
      public PlaylistEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfChannelCount = CursorUtil.getColumnIndexOrThrow(_cursor, "channelCount");
          final int _cursorIndexOfLastUpdated = CursorUtil.getColumnIndexOrThrow(_cursor, "lastUpdated");
          final int _cursorIndexOfEpgUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "epgUrl");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
          final int _cursorIndexOfServerUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "serverUrl");
          final int _cursorIndexOfMacAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "macAddress");
          final PlaylistEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpUrl;
            if (_cursor.isNull(_cursorIndexOfUrl)) {
              _tmpUrl = null;
            } else {
              _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final int _tmpChannelCount;
            _tmpChannelCount = _cursor.getInt(_cursorIndexOfChannelCount);
            final long _tmpLastUpdated;
            _tmpLastUpdated = _cursor.getLong(_cursorIndexOfLastUpdated);
            final String _tmpEpgUrl;
            if (_cursor.isNull(_cursorIndexOfEpgUrl)) {
              _tmpEpgUrl = null;
            } else {
              _tmpEpgUrl = _cursor.getString(_cursorIndexOfEpgUrl);
            }
            final String _tmpUsername;
            if (_cursor.isNull(_cursorIndexOfUsername)) {
              _tmpUsername = null;
            } else {
              _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            }
            final String _tmpPassword;
            if (_cursor.isNull(_cursorIndexOfPassword)) {
              _tmpPassword = null;
            } else {
              _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
            }
            final String _tmpServerUrl;
            if (_cursor.isNull(_cursorIndexOfServerUrl)) {
              _tmpServerUrl = null;
            } else {
              _tmpServerUrl = _cursor.getString(_cursorIndexOfServerUrl);
            }
            final String _tmpMacAddress;
            if (_cursor.isNull(_cursorIndexOfMacAddress)) {
              _tmpMacAddress = null;
            } else {
              _tmpMacAddress = _cursor.getString(_cursorIndexOfMacAddress);
            }
            _result = new PlaylistEntity(_tmpId,_tmpName,_tmpUrl,_tmpFilePath,_tmpType,_tmpChannelCount,_tmpLastUpdated,_tmpEpgUrl,_tmpUsername,_tmpPassword,_tmpServerUrl,_tmpMacAddress);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
