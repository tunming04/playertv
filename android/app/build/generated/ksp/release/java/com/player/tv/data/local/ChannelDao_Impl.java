package com.player.tv.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class ChannelDao_Impl implements ChannelDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ChannelEntity> __insertionAdapterOfChannelEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteChannelsByPlaylist;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllChannels;

  public ChannelDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfChannelEntity = new EntityInsertionAdapter<ChannelEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `channels` (`id`,`name`,`url`,`logo`,`groupTitle`,`tvgId`,`tvgName`,`isRadio`,`streamFormat`,`streamType`,`playlistId`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChannelEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getUrl());
        if (entity.getLogo() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getLogo());
        }
        if (entity.getGroupTitle() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getGroupTitle());
        }
        if (entity.getTvgId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getTvgId());
        }
        if (entity.getTvgName() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getTvgName());
        }
        final int _tmp = entity.isRadio() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindString(9, entity.getStreamFormat());
        statement.bindString(10, entity.getStreamType());
        statement.bindString(11, entity.getPlaylistId());
        statement.bindLong(12, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfDeleteChannelsByPlaylist = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM channels WHERE playlistId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllChannels = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM channels";
        return _query;
      }
    };
  }

  @Override
  public Object insertChannels(final List<ChannelEntity> channels,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfChannelEntity.insert(channels);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteChannelsByPlaylist(final String playlistId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteChannelsByPlaylist.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, playlistId);
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
          __preparedStmtOfDeleteChannelsByPlaylist.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllChannels(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllChannels.acquire();
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
          __preparedStmtOfDeleteAllChannels.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ChannelEntity>> getChannelsByPlaylist(final String playlistId) {
    final String _sql = "SELECT * FROM channels WHERE playlistId = ? ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, playlistId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"channels"}, new Callable<List<ChannelEntity>>() {
      @Override
      @NonNull
      public List<ChannelEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfLogo = CursorUtil.getColumnIndexOrThrow(_cursor, "logo");
          final int _cursorIndexOfGroupTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "groupTitle");
          final int _cursorIndexOfTvgId = CursorUtil.getColumnIndexOrThrow(_cursor, "tvgId");
          final int _cursorIndexOfTvgName = CursorUtil.getColumnIndexOrThrow(_cursor, "tvgName");
          final int _cursorIndexOfIsRadio = CursorUtil.getColumnIndexOrThrow(_cursor, "isRadio");
          final int _cursorIndexOfStreamFormat = CursorUtil.getColumnIndexOrThrow(_cursor, "streamFormat");
          final int _cursorIndexOfStreamType = CursorUtil.getColumnIndexOrThrow(_cursor, "streamType");
          final int _cursorIndexOfPlaylistId = CursorUtil.getColumnIndexOrThrow(_cursor, "playlistId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ChannelEntity> _result = new ArrayList<ChannelEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChannelEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpLogo;
            if (_cursor.isNull(_cursorIndexOfLogo)) {
              _tmpLogo = null;
            } else {
              _tmpLogo = _cursor.getString(_cursorIndexOfLogo);
            }
            final String _tmpGroupTitle;
            if (_cursor.isNull(_cursorIndexOfGroupTitle)) {
              _tmpGroupTitle = null;
            } else {
              _tmpGroupTitle = _cursor.getString(_cursorIndexOfGroupTitle);
            }
            final String _tmpTvgId;
            if (_cursor.isNull(_cursorIndexOfTvgId)) {
              _tmpTvgId = null;
            } else {
              _tmpTvgId = _cursor.getString(_cursorIndexOfTvgId);
            }
            final String _tmpTvgName;
            if (_cursor.isNull(_cursorIndexOfTvgName)) {
              _tmpTvgName = null;
            } else {
              _tmpTvgName = _cursor.getString(_cursorIndexOfTvgName);
            }
            final boolean _tmpIsRadio;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRadio);
            _tmpIsRadio = _tmp != 0;
            final String _tmpStreamFormat;
            _tmpStreamFormat = _cursor.getString(_cursorIndexOfStreamFormat);
            final String _tmpStreamType;
            _tmpStreamType = _cursor.getString(_cursorIndexOfStreamType);
            final String _tmpPlaylistId;
            _tmpPlaylistId = _cursor.getString(_cursorIndexOfPlaylistId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ChannelEntity(_tmpId,_tmpName,_tmpUrl,_tmpLogo,_tmpGroupTitle,_tmpTvgId,_tmpTvgName,_tmpIsRadio,_tmpStreamFormat,_tmpStreamType,_tmpPlaylistId,_tmpCreatedAt);
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
  public Flow<List<ChannelEntity>> getChannelsByGroup(final String playlistId,
      final String groupTitle) {
    final String _sql = "SELECT * FROM channels WHERE playlistId = ? AND groupTitle = ? ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, playlistId);
    _argIndex = 2;
    _statement.bindString(_argIndex, groupTitle);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"channels"}, new Callable<List<ChannelEntity>>() {
      @Override
      @NonNull
      public List<ChannelEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfLogo = CursorUtil.getColumnIndexOrThrow(_cursor, "logo");
          final int _cursorIndexOfGroupTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "groupTitle");
          final int _cursorIndexOfTvgId = CursorUtil.getColumnIndexOrThrow(_cursor, "tvgId");
          final int _cursorIndexOfTvgName = CursorUtil.getColumnIndexOrThrow(_cursor, "tvgName");
          final int _cursorIndexOfIsRadio = CursorUtil.getColumnIndexOrThrow(_cursor, "isRadio");
          final int _cursorIndexOfStreamFormat = CursorUtil.getColumnIndexOrThrow(_cursor, "streamFormat");
          final int _cursorIndexOfStreamType = CursorUtil.getColumnIndexOrThrow(_cursor, "streamType");
          final int _cursorIndexOfPlaylistId = CursorUtil.getColumnIndexOrThrow(_cursor, "playlistId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ChannelEntity> _result = new ArrayList<ChannelEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChannelEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpLogo;
            if (_cursor.isNull(_cursorIndexOfLogo)) {
              _tmpLogo = null;
            } else {
              _tmpLogo = _cursor.getString(_cursorIndexOfLogo);
            }
            final String _tmpGroupTitle;
            if (_cursor.isNull(_cursorIndexOfGroupTitle)) {
              _tmpGroupTitle = null;
            } else {
              _tmpGroupTitle = _cursor.getString(_cursorIndexOfGroupTitle);
            }
            final String _tmpTvgId;
            if (_cursor.isNull(_cursorIndexOfTvgId)) {
              _tmpTvgId = null;
            } else {
              _tmpTvgId = _cursor.getString(_cursorIndexOfTvgId);
            }
            final String _tmpTvgName;
            if (_cursor.isNull(_cursorIndexOfTvgName)) {
              _tmpTvgName = null;
            } else {
              _tmpTvgName = _cursor.getString(_cursorIndexOfTvgName);
            }
            final boolean _tmpIsRadio;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRadio);
            _tmpIsRadio = _tmp != 0;
            final String _tmpStreamFormat;
            _tmpStreamFormat = _cursor.getString(_cursorIndexOfStreamFormat);
            final String _tmpStreamType;
            _tmpStreamType = _cursor.getString(_cursorIndexOfStreamType);
            final String _tmpPlaylistId;
            _tmpPlaylistId = _cursor.getString(_cursorIndexOfPlaylistId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ChannelEntity(_tmpId,_tmpName,_tmpUrl,_tmpLogo,_tmpGroupTitle,_tmpTvgId,_tmpTvgName,_tmpIsRadio,_tmpStreamFormat,_tmpStreamType,_tmpPlaylistId,_tmpCreatedAt);
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
  public Flow<List<ChannelEntity>> searchChannels(final String playlistId, final String query) {
    final String _sql = "SELECT * FROM channels WHERE playlistId = ? AND (name LIKE '%' || ? || '%' OR tvgName LIKE '%' || ? || '%')";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, playlistId);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    _argIndex = 3;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"channels"}, new Callable<List<ChannelEntity>>() {
      @Override
      @NonNull
      public List<ChannelEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfLogo = CursorUtil.getColumnIndexOrThrow(_cursor, "logo");
          final int _cursorIndexOfGroupTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "groupTitle");
          final int _cursorIndexOfTvgId = CursorUtil.getColumnIndexOrThrow(_cursor, "tvgId");
          final int _cursorIndexOfTvgName = CursorUtil.getColumnIndexOrThrow(_cursor, "tvgName");
          final int _cursorIndexOfIsRadio = CursorUtil.getColumnIndexOrThrow(_cursor, "isRadio");
          final int _cursorIndexOfStreamFormat = CursorUtil.getColumnIndexOrThrow(_cursor, "streamFormat");
          final int _cursorIndexOfStreamType = CursorUtil.getColumnIndexOrThrow(_cursor, "streamType");
          final int _cursorIndexOfPlaylistId = CursorUtil.getColumnIndexOrThrow(_cursor, "playlistId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ChannelEntity> _result = new ArrayList<ChannelEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChannelEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpLogo;
            if (_cursor.isNull(_cursorIndexOfLogo)) {
              _tmpLogo = null;
            } else {
              _tmpLogo = _cursor.getString(_cursorIndexOfLogo);
            }
            final String _tmpGroupTitle;
            if (_cursor.isNull(_cursorIndexOfGroupTitle)) {
              _tmpGroupTitle = null;
            } else {
              _tmpGroupTitle = _cursor.getString(_cursorIndexOfGroupTitle);
            }
            final String _tmpTvgId;
            if (_cursor.isNull(_cursorIndexOfTvgId)) {
              _tmpTvgId = null;
            } else {
              _tmpTvgId = _cursor.getString(_cursorIndexOfTvgId);
            }
            final String _tmpTvgName;
            if (_cursor.isNull(_cursorIndexOfTvgName)) {
              _tmpTvgName = null;
            } else {
              _tmpTvgName = _cursor.getString(_cursorIndexOfTvgName);
            }
            final boolean _tmpIsRadio;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRadio);
            _tmpIsRadio = _tmp != 0;
            final String _tmpStreamFormat;
            _tmpStreamFormat = _cursor.getString(_cursorIndexOfStreamFormat);
            final String _tmpStreamType;
            _tmpStreamType = _cursor.getString(_cursorIndexOfStreamType);
            final String _tmpPlaylistId;
            _tmpPlaylistId = _cursor.getString(_cursorIndexOfPlaylistId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ChannelEntity(_tmpId,_tmpName,_tmpUrl,_tmpLogo,_tmpGroupTitle,_tmpTvgId,_tmpTvgName,_tmpIsRadio,_tmpStreamFormat,_tmpStreamType,_tmpPlaylistId,_tmpCreatedAt);
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
  public Flow<List<ChannelEntity>> searchAllChannels(final String query) {
    final String _sql = "SELECT * FROM channels WHERE name LIKE '%' || ? || '%' OR tvgName LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"channels"}, new Callable<List<ChannelEntity>>() {
      @Override
      @NonNull
      public List<ChannelEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfLogo = CursorUtil.getColumnIndexOrThrow(_cursor, "logo");
          final int _cursorIndexOfGroupTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "groupTitle");
          final int _cursorIndexOfTvgId = CursorUtil.getColumnIndexOrThrow(_cursor, "tvgId");
          final int _cursorIndexOfTvgName = CursorUtil.getColumnIndexOrThrow(_cursor, "tvgName");
          final int _cursorIndexOfIsRadio = CursorUtil.getColumnIndexOrThrow(_cursor, "isRadio");
          final int _cursorIndexOfStreamFormat = CursorUtil.getColumnIndexOrThrow(_cursor, "streamFormat");
          final int _cursorIndexOfStreamType = CursorUtil.getColumnIndexOrThrow(_cursor, "streamType");
          final int _cursorIndexOfPlaylistId = CursorUtil.getColumnIndexOrThrow(_cursor, "playlistId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ChannelEntity> _result = new ArrayList<ChannelEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChannelEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpUrl;
            _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            final String _tmpLogo;
            if (_cursor.isNull(_cursorIndexOfLogo)) {
              _tmpLogo = null;
            } else {
              _tmpLogo = _cursor.getString(_cursorIndexOfLogo);
            }
            final String _tmpGroupTitle;
            if (_cursor.isNull(_cursorIndexOfGroupTitle)) {
              _tmpGroupTitle = null;
            } else {
              _tmpGroupTitle = _cursor.getString(_cursorIndexOfGroupTitle);
            }
            final String _tmpTvgId;
            if (_cursor.isNull(_cursorIndexOfTvgId)) {
              _tmpTvgId = null;
            } else {
              _tmpTvgId = _cursor.getString(_cursorIndexOfTvgId);
            }
            final String _tmpTvgName;
            if (_cursor.isNull(_cursorIndexOfTvgName)) {
              _tmpTvgName = null;
            } else {
              _tmpTvgName = _cursor.getString(_cursorIndexOfTvgName);
            }
            final boolean _tmpIsRadio;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRadio);
            _tmpIsRadio = _tmp != 0;
            final String _tmpStreamFormat;
            _tmpStreamFormat = _cursor.getString(_cursorIndexOfStreamFormat);
            final String _tmpStreamType;
            _tmpStreamType = _cursor.getString(_cursorIndexOfStreamType);
            final String _tmpPlaylistId;
            _tmpPlaylistId = _cursor.getString(_cursorIndexOfPlaylistId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ChannelEntity(_tmpId,_tmpName,_tmpUrl,_tmpLogo,_tmpGroupTitle,_tmpTvgId,_tmpTvgName,_tmpIsRadio,_tmpStreamFormat,_tmpStreamType,_tmpPlaylistId,_tmpCreatedAt);
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
  public Flow<List<String>> getGroups(final String playlistId) {
    final String _sql = "SELECT DISTINCT groupTitle FROM channels WHERE playlistId = ? AND groupTitle IS NOT NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, playlistId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"channels"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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
  public Object getChannelCount(final String playlistId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM channels WHERE playlistId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, playlistId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
