import 'package:flutter/material.dart';
import '../models/collage_model.dart';
import '../widgets/collage_piece_widget.dart';
import '../utils/shape_processor.dart';

class CollageEditorScreen extends StatefulWidget {
  final String? shapePath;
  const CollageEditorScreen({super.key, this.shapePath});

  @override
  State<CollageEditorScreen> createState() => _CollageEditorScreenState();
}

class _CollageEditorScreenState extends State<CollageEditorScreen> {
  List<CollageModel> _pieces = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    // Simulate loading/processing shape
    _loadShapeAndImages();
  }

  Future<void> _loadShapeAndImages() async {
    if (widget.shapePath == null) {
      setState(() => _isLoading = false);
      return;
    }

    try {
      final processor = ShapeProcessor();
      // Remove 'assets/' prefix if it's already in the path from HomeScreen, 
      // but rootBundle.load might need the full key.
      // In HomeScreen we passed 'assets/images/m_shape.png'.
      // If the file is just an icon, it might not be the mask. 
      // Ideally we should use the mask from 'assets/shapes/...'.
      // For now, let's try to process the icon itself as a test.
      
      final points = await processor.processShape(widget.shapePath!);
      
      final newPieces = points.map((point) {
        return CollageModel(
          position: point,
          angle: (point.dx + point.dy) % 0.5, // Random-ish angle
          size: const Size(40, 40), // Smaller size for dense points
          borderColor: Colors.white,
          borderSize: 2.0,
        );
      }).toList();

      if (mounted) {
        setState(() {
          _pieces = newPieces;
          _isLoading = false;
        });
      }
    } catch (e) {
      debugPrint("Error processing shape: $e");
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Edit Collage'),
        actions: [
          IconButton(
            icon: const Icon(Icons.save),
            onPressed: () {
              // TODO: Save functionality
            },
          ),
          IconButton(
            icon: const Icon(Icons.share),
            onPressed: () {
              // TODO: Share functionality
            },
          )
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : Stack(
              children: [
                // Background
                Container(color: Colors.white),
                
                // Collage Pieces
                ..._pieces.map((piece) {
                  return CollagePieceWidget(
                    model: piece,
                    onTap: () {
                      // Handle selection
                    },
                    onScaleUpdate: (details) {
                      // Handle dragging/scaling
                      setState(() {
                        piece.position += details.focalPointDelta;
                        piece.angle += details.rotation;
                        // piece.size *= details.scale; // Scale logic needs to be careful not to explode
                      });
                    },
                  );
                }),
              ],
            ),
       bottomNavigationBar: BottomAppBar(
         child: Row(
           mainAxisAlignment: MainAxisAlignment.spaceAround,
           children: [
             IconButton(icon: const Icon(Icons.add_a_photo), onPressed: () {}),
             IconButton(icon: const Icon(Icons.format_shapes), onPressed: () {}),
             IconButton(icon: const Icon(Icons.color_lens), onPressed: () {}),
             IconButton(icon: const Icon(Icons.settings), onPressed: () {}),
           ],
         ),
       ),
    );
  }
}
