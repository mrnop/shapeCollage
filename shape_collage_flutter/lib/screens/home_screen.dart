import 'package:flutter/material.dart';
import '../widgets/app_drawer.dart';
import 'collage_editor_screen.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Shape Collage'),
      ),
      drawer: const AppDrawer(),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildSectionHeader(context, "Arts"),
            _buildArtsGrid(context),
            _buildSectionHeader(context, "Sponsored"),
            // Placeholder for Ads
            Container(
              height: 100,
              color: Colors.grey[200],
              child: const Center(child: Text('Ad Banner')),
            ),
             _buildSectionHeader(context, "Try More"),
             // Placeholder for More Apps
          ],
        ),
      ),
    );
  }

  Widget _buildSectionHeader(BuildContext context, String title) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Text(
        title,
        style: Theme.of(context).textTheme.titleLarge?.copyWith(
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }

  Widget _buildArtsGrid(BuildContext context) {
    // Mock data based on the features
    final arts = [
      {'title': 'Shape Collage', 'icon': 'assets/images/m_shape.png', 'color': Colors.redAccent},
      {'title': 'Name Collage', 'icon': 'assets/images/m_custom.png', 'color': Colors.blueAccent},
      {'title': 'Frame Collage', 'icon': 'assets/images/frame1.png', 'color': Colors.green},
    ];

    return GridView.builder(
      physics: const NeverScrollableScrollPhysics(),
      shrinkWrap: true,
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 2,
        childAspectRatio: 1.0,
      ),
      itemCount: arts.length,
      itemBuilder: (context, index) {
        final art = arts[index];
        return Card(
          elevation: 4,
          margin: const EdgeInsets.all(8),
          child: InkWell(
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => CollageEditorScreen(
                    shapePath: art['icon'] as String,
                  ),
                ),
              );
            },
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.all(16.0),
                    // Use Image.asset if available, else Icon
                    child: Image.asset(
                      art['icon'] as String,
                      errorBuilder: (context, error, stackTrace) => Icon(Icons.image, size: 50, color: art['color'] as Color),
                    ),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Text(
                    art['title'] as String,
                    textAlign: TextAlign.center,
                    style: const TextStyle(fontWeight: FontWeight.bold),
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}
