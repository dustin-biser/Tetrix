#version 330 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

in vec4 in_Position;
in vec4 in_Color;
in vec3 in_TextureCoord;

out vec4 pass_Color;
out vec3 pass_TextureCoord;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * in_Position;
	
	pass_Color = in_Color;
	pass_TextureCoord = in_TextureCoord;
}