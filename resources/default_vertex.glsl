#version 150 core

in vec3 in_position;
in vec2 in_texcoord;
in vec3 in_normal;

out vec2 varying_textureCoord;
out vec4 varying_position; // position of the vertex (and fragment) in world space
out vec3 varying_normal; // surface normal vector in world space

uniform mat4 model;
uniform mat3 model_3x3_inv_transp;
uniform mat4 view;
uniform mat4 projection;

void main() {
    mat4 mvp = projection * view * model;
	
	varying_textureCoord = in_texcoord;
	gl_Position = mvp * vec4(in_position, 1.0);
	
	varying_position = model * vec4(in_position, 1.0);
	varying_normal = normalize(model_3x3_inv_transp * in_normal);
}
