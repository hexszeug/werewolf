import Players, { PlayerList } from '../players/Players';

export const GameStartView = (props: { players: PlayerList }) => {
	return <Players players={props.players} />;
};
