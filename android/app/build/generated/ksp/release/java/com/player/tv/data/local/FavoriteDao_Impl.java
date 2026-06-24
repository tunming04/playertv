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
import java.lang.Boolean;
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
public final class FavoriteDao_Impl implements FavoriteDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FavoriteEntity> __insertionAdapterOfFavoriteEntity;

  private final EntityDeletionOrUpdateAdapter<FavoriteEntity> __deletionAdapterOfFavoriteEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteFavoriteByChannelId;

  public FavoriteDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFavoriteEntity = new EntityInsertionAdapter<FavoriteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `favorites` (`id`,`channelId`,`playlistId`,`createdAt`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FavoriteEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getChannelId());
        statement.bindString(3, entity.getPlaylistId());
        statement.bindLong(4, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfFavoriteEntity = new EntityDeletionOrUpdateAdapter<FavoriteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `favorites` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FavoriteEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteFavoriteByChannelId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM favorites WHERE channelId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertFavorite(final FavoriteEntity favorite,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFavoriteEntity.insert(favorite);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteFavorite(final FavoriteEntity favorite,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFavoriteEntity.handle(favorite);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteFavoriteByChannelId(final String channelId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteFavoriteByChannelId.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, channelId);
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
          __preparedStmtOfDeleteFavoriteByChannelId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<FavoriteEntity>> getAllFavorites() {
    final String _sql = "SELECT * FROM favorites ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"favorites"}, new Callable<List<FavoriteEntity>>() {
      @Override
      @NonNull
      public List<FavoriteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfChannelId = CursorUtil.getColumnIndexOrThrow(_cursor, "channelId");
          final int _cursorIndexOfPlaylistId = CursorUtil.getColumnIndexOrThrow(_cursor, "playlistId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<FavoriteEntity> _result = new ArrayList<FavoriteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FavoriteEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpChannelId;
            _tmpChannelId = _cursor.getString(_cursorIndexOfChannelId);
            final String _tmpPlaylistId;
            _tmpPlaylistId = _cursor.getString(_cursorIndexOfPlaylistId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new FavoriteEntity(_tmpId,_tmpChannelId,_tmpPlaylistId,_tmpCreatedAt);
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
  public Object getFavorite(final String channelId,
      final Continuation<? super FavoriteEntity> $completion) {
    final String _sql = "SELECT * FROM favorites WHERE channelId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, channelId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FavoriteEntity>() {
      @Override
      @Nullable
      public FavoriteEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfChannelId = CursorUtil.getColumnIndexOrThrow(_cursor, "channelId");
          final int _cursorIndexOfPlaylistId = CursorUtil.getColumnIndexOrThrow(_cursor, "playlistId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final FavoriteEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpChannelId;
            _tmpChannelId = _cursor.getString(_cursorIndexOfChannelId);
            final String _tmpPlaylistId;
            _tmpPlaylistId = _cursor.getString(_cursorIndexOfPlaylistId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new FavoriteEntity(_tmpId,_tmpChannelId,_tmpPlaylistId,_tmpCreatedAt);
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

  @Override
  public Flow<FavoriteEntity> observeFavorite(final String channelId) {
    final String _sql = "SELECT * FROM favorites WHERE channelId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, channelId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"favorites"}, new Callable<FavoriteEntity>() {
      @Override
      @Nullable
      public FavoriteEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfChannelId = CursorUtil.getColumnIndexOrThrow(_cursor, "channelId");
          final int _cursorIndexOfPlaylistId = CursorUtil.getColumnIndexOrThrow(_cursor, "playlistId");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final FavoriteEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpChannelId;
            _tmpChannelId = _cursor.getString(_cursorIndexOfChannelId);
            final String _tmpPlaylistId;
            _tmpPlaylistId = _cursor.getString(_cursorIndexOfPlaylistId);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new FavoriteEntity(_tmpId,_tmpChannelId,_tmpPlaylistId,_tmpCreatedAt);
          } else {
            _result = null;
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
  public Flow<Boolean> isFavorite(final String channelId) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM favorites WHERE channelId = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, channelId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"favorites"}, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp != 0;
          } else {
            _result = false;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
