#version 330 core

uniform sampler2DArray texture_diffuse;

in vec4 pass_Color;
in vec3 pass_TextureCoord;

out vec4 out_Color;

void main(void) {
	out_Color = texture(texture_diffuse, pass_TextureCoord);
}