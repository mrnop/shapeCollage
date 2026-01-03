import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart';

class DatabaseHelper {
  static final DatabaseHelper instance = DatabaseHelper._init();
  static Database? _database;

  DatabaseHelper._init();

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDB('shape_collage.db');
    return _database!;
  }

  Future<Database> _initDB(String filePath) async {
    final dbPath = await getDatabasesPath();
    final path = join(dbPath, filePath);

    return await openDatabase(path, version: 1, onCreate: _createDB);
  }

  Future _createDB(Database db, int version) async {
    const idType = 'INTEGER PRIMARY KEY AUTOINCREMENT';
    const textType = 'TEXT';
    const boolType = 'INTEGER'; // 0 or 1
    const integerType = 'INTEGER';

    await db.execute('''
CREATE TABLE stickers (
  id $idType,
  refNo $textType,
  stickerName $textType,
  remoteUrl $textType,
  file $textType,
  active $boolType,
  created $integerType
)
''');

    await db.execute('''
CREATE TABLE shape_info (
  id $idType,
  info $textType,
  folder $textType,
  path $textType,
  type $textType,
  created $integerType
)
''');
  }

  Future<void> close() async {
    final db = await instance.database;
    db.close();
  }
}
