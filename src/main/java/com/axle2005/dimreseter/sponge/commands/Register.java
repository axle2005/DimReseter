package com.axle2005.dimreseter.sponge.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import com.axle2005.dimreseter.sponge.Config;
import com.axle2005.dimreseter.sponge.DimReseter;



public class Register {

	public Register(DimReseter plugin, Config config) {
		CommandSpec add = CommandSpec.builder().permission("dimreset.admin")
				.description(Text.of("Add Dim to Config"))
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("Dim|Here"))))
				.executor(new CommandDimAdd(plugin, config)).build();
		
		CommandSpec reset = CommandSpec.builder().permission("dimreset.admin")
				.description(Text.of("Reset Dimension"))
				.arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("DIM"))))
				.executor(new CommandDimReset(plugin)).build();

		CommandSpec dimreset = CommandSpec.builder().description(Text.of("dimreseter")).child(add, "add").child(reset, "reset")
				.build();

		Sponge.getCommandManager().register(plugin, dimreset, "dimreseter","toastyreset");
	}
}